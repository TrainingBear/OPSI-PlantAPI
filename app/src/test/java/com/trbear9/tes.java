package com.trbear9;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class tes {

    private static final Logger log = LoggerFactory.getLogger("TEST METHOD/DLL");

    @Test
    public void test (){
        Enumerate instance1 = Enumerate.A;
        float primitive = 1f;
        Float reference = 1f;
        assert instance1 instanceof Object;
        assert instance1 instanceof Enumerate;
        assert reference instanceof Object;
    }

    @Test
    public void test2 (){
        String string = "SMA NEGERI 1 AMBARAWA, OPSI 2025 - OPSI SMANEGA";
        String sub1 = "NEGERI";
        String sub2 = "OPSI";
        String string2 = "AMBARAWA";
        String sub3 = "AMBA";
        String sub4 = "AMBARAWA";
        String sub5 = "RAWA";
        log.info("{} = {}", sub1, string.indexOf(sub1));
        log.info("{} = {}", sub2, string.indexOf(sub2));
        log.info("{} = {}", sub3, string2.indexOf(sub3));
        log.info("{} = {}", sub4, string2.indexOf(sub4));
        log.info("{} = {}", sub5, string2.indexOf(sub5));
        string2.contains(sub5);
        string2.contains(sub4);
    }

    @Test
    public void test3 (){
        System.getenv().forEach((k, v) -> log.info("{} = {}", k, v));
        String key = System.getenv("API_KEY");
        assert key != null;
        log.info("OPEN_AI_KEY = {}", key);
    }

    public enum Enumerate {
        A, B, C;
    }

    public interface Interface {
    }
}
