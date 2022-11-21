package hello.springtx.apply;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;

@Slf4j
@SpringBootTest
public class InitTxTest {

    @Autowired Hello hello;

    @Test
    void go(){
        //@PostConstruct 초기화 코드는 스프링 초기화 시점에 호출된다.
        //따라서 hello.initV1() 을 직접 호출하면 당연히 트랜잭션이 적용된다. (런타임 시점)

        //@EventListener(ApplicationReadyEvent.class)
        //트랜잭션 AOP를 포함한 스프링이 컨테이너가 완전히 생성된 후에 호출된다.
    }

    @TestConfiguration
    static class InitTxTestConfig {

        @Bean
        Hello hello(){
            return new Hello();
        }
    }

    static class Hello {

        @PostConstruct
        @Transactional
        public void initV1(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init @PostConstruct tx active={}", txActive);
        }

        @EventListener(ApplicationReadyEvent.class)
        @Transactional
        public void initV2(){
            boolean txActive = TransactionSynchronizationManager.isActualTransactionActive();
            log.info("Hello init ApplicationReadyEvent tx active={}", txActive);
        }
    }
}
