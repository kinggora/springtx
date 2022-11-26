package hello.springtx.propagation;

import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.UnexpectedRollbackException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired MemberRepository memberRepository;
    @Autowired LogRepository logRepository;

    /**
     * memberService    @Transactional  OFF
     * memberRepository @Transactional  ON
     * logRepository    @Transactional  ON
     */
    @Test
    void outerTxOff_success(){
        //Given
        String username = "outerTxOff_success";

        //When
        memberService.joinV1(username);

        //Then: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * memberService    @Transactional  OFF
     * memberRepository @Transactional  ON
     * logRepository    @Transactional  ON
     */
    @Test
    void outerTxOff_fail(){
        //Given
        String username = "로그예외_outerTxOff_fail";

        //When
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //Then: log 데이터가 롤백되고, member 데이터는 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * memberService    @Transactional  ON
     * memberRepository @Transactional  OFF
     * logRepository    @Transactional  OFF
     */
    @Test
    void singleTx(){
        //Given
        String username = "singleTx";

        //When
        memberService.joinV1(username);

        //Then: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * MemberService    @Transactional  ON
     * MemberRepository @Transactional  ON
     * LogRepository    @Transactional  ON
     */
    @Test
    void outerTxOn_success() {
        //Given
        String username = "outerTxOn_success";

        //When
        memberService.joinV1(username);

        //Then: 모든 데이터가 정상 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isPresent());
    }

    /**
     * MemberService    @Transactional  ON
     * MemberRepository @Transactional  ON
     * LogRepository    @Transactional  ON  Exception
     */
    @Test
    void outerTxOn_fail() {
        //Given
        String username = "로그예외_outerTxOn_fail";

        //When
        assertThatThrownBy(() -> memberService.joinV1(username))
                .isInstanceOf(RuntimeException.class);

        //Then: 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * MemberService    @Transactional  ON
     * MemberRepository @Transactional  ON
     * LogRepository    @Transactional  ON  Exception
     */
    @Test
    void recoverException_fail() {
        //Given
        String username = "로그예외_recoverException_fail";

        //When
        assertThatThrownBy(() -> memberService.joinV2(username))
                .isInstanceOf(UnexpectedRollbackException.class);

        //Then: 모든 데이터가 롤백된다.
        assertTrue(memberRepository.find(username).isEmpty());
        assertTrue(logRepository.find(username).isEmpty());
    }

    /**
     * MemberService    @Transactional  ON
     * MemberRepository @Transactional  ON
     * LogRepository    @Transactional  ON(REQUIRES_NEW)  Exception
     */
    @Test
    void recoverException_success(){
        //Given
        String username = "로그예외_recoverException_success";

        //When
        memberService.joinV2(username);

        //Then: log 데이터가 롤백되고, member 데이터는 저장된다.
        assertTrue(memberRepository.find(username).isPresent());
        assertTrue(logRepository.find(username).isEmpty());
    }
}