package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.error.AlreadyExistsException;
import jpabook.jpashop.repository.member.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional
public class MemberServiceTest {

    @Autowired MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
//    @Autowired EntityManager em;

    @Test
//    @Rollback(value = false)
    public void 회원가입() {
        //given
        Member member = Member.builder()
                .name("kim")
                .build();

        //when
        Long saveId = memberService.join(member);

        //then
//        em.flush();
        assertThat(member).isEqualTo(memberRepository.findById(saveId).get());
    }

    @Test//(expected = IllegalStateException.class)
    public void 중복_회원_예외() throws Exception {
        Member member1 = Member.builder().name("kim").build();
        Member member2 = Member.builder().name("kim").build();

        memberService.join(member1);
//        memberService.join(member2);

        Assertions.assertThatThrownBy(() -> memberService.join(member2))
                .isInstanceOf(AlreadyExistsException.class);

//        fail("예외가 발생해야 한다.");
    }

}