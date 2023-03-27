package review.hairshop.common.utils;


import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;
import review.hairshop.member.dto.KakaoAuthDto;
import review.hairshop.member.dto.response.NaverLocalResponseDto;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Component
public class WebClientUtil {


    private WebClient createWebClient(String url, String accessToken){
        return WebClient.builder()
                .baseUrl(url)
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .build();
    }

    private HttpClient createHttpClient(){
        return HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofMillis(5000))
                .doOnConnected(
                        conn -> conn.addHandlerLast(new ReadTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                                .addHandlerLast(new WriteTimeoutHandler(5000, TimeUnit.MILLISECONDS))
                );
    }



    public Flux<KakaoAuthDto> sendRequest(String url, String accessToken){
        HttpClient httpClient = createHttpClient();
        WebClient webClient = createWebClient(url, accessToken);

        return webClient.get()
                .retrieve()
                .bodyToFlux(KakaoAuthDto.class);
    }




}
