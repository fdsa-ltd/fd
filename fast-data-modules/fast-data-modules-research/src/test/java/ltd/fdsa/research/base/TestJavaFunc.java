package ltd.fdsa.research.base;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;


/**
 * @author zhumingwu
 * @ClassName:
 * @description:
 * @since 2020-10-28
 **/
@RunWith(SpringRunner.class)
@Slf4j
public class TestJavaFunc {
    @FunctionalInterface
    interface SFunc<T> {
        String[] apply(T var1);
    }

    @Test
    public void Profit() {

    }
}
