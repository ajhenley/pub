package com.learningbycoding.pub.integration.github;

import com.learningbycoding.pub.PubProperties;
import java.io.IOException;
import java.time.Instant;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;

import org.springframework.boot.actuate.metrics.CounterService;
import org.springframework.boot.jackson.JsonObjectDeserializer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GithubClient {

    private static final Pattern LINK_PATTERN = Pattern.compile("<(.+)>; rel=\"(.+)\"");

    private final CounterService counterService;

    private final RestTemplate restTemplate;

    public GithubClient(CounterService counterService, RestTemplateBuilder restTemplateBuilder,
                        PubProperties properties) {
        this.counterService = counterService;
        this.restTemplate = restTemplateBuilder.additionalCustomizers(rt ->
                rt.getInterceptors().add(new GithubAppTokenInterceptor(properties.getGithub().getToken()))).build();
    }

    @Cacheable("github.commits")
    public List<Commit> getRecentCommits(String organization, String project) {
        ResponseEntity<Commit[]> response = doGetRecentCommit(organization, project);
        return Arrays.asList(response.getBody());
    }

    @Cacheable("github.polishCommit")
    public Commit getRecentPolishCommit(String organization, String project) {
        ResponseEntity<Commit[]> page = doGetRecentCommit(organization, project);
        for (int i = 0; i < 4; i++) {
            Commit commit = Stream.of(page.getBody())
                    .filter(c -> c.getMessage().toLowerCase().contains("polish"))
                    .findFirst()
                    .orElse(null);

            if (commit != null) {
                return commit;
            }
            String nextPage = parseLinkHeader(page).get("next");
            if (nextPage != null) {
                page = invoke(createRequestEntity(nextPage), Commit[].class);
            }
            else {
                break;
            }
        }
        return null;
    }

    private ResponseEntity<Commit[]> doGetRecentCommit(String organization, String project) {
        String url = String.format(
                "https://api.github.com/repos/%s/%s/commits", organization, project);
        return invoke(createRequestEntity(url), Commit[].class);
    }

    @Cacheable("github.user")
    public GithubUser getUser(String githubId) {
        return invoke(createRequestEntity(
                String.format("https://api.github.com/users/%s", githubId)), GithubUser.class).getBody();
    }

    private <T> ResponseEntity<T> invoke(RequestEntity<?> request, Class<T> type) {
        this.counterService.increment("cfp.github.requests");
        try {
            return this.restTemplate.exchange(request, type);
        }
        catch (RestClientException ex) {
            this.counterService.increment("cfp.github.failures");
            throw ex;
        }
    }

    private RequestEntity<?> createRequestEntity(String url) {
        try {
            return RequestEntity.get(new URI(url))
                    .accept(MediaType.APPLICATION_JSON).build();
        }
        catch (URISyntaxException ex) {
            throw new IllegalStateException("Invalid URL " + url, ex);
        }
    }

    private Map<String, String> parseLinkHeader(ResponseEntity<?> response) {
        List<String> links = response.getHeaders().get(HttpHeaders.LINK);
        if (links == null) {
            return Collections.emptyMap();
        }
        Map<String, String> result = new HashMap<>();
        for (String link : links) {
            Matcher matcher = LINK_PATTERN.matcher(link.trim());
            if (matcher.matches()) {
                result.put(matcher.group(2), matcher.group(1));
            }
        }
        return result;
    }


    private static class GithubAppTokenInterceptor implements ClientHttpRequestInterceptor {

        private final String token;

        GithubAppTokenInterceptor(String token) {
            this.token = token;
        }

        @Override
        public ClientHttpResponse intercept(HttpRequest httpRequest, byte[] bytes,
                                            ClientHttpRequestExecution clientHttpRequestExecution) throws IOException {
            if (StringUtils.hasText(this.token)) {
                byte[] basicAuthValue = this.token.getBytes(StandardCharsets.UTF_8);
                httpRequest.getHeaders().set(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString(basicAuthValue));
            }
            return clientHttpRequestExecution.execute(httpRequest, bytes);
        }

    }

}
