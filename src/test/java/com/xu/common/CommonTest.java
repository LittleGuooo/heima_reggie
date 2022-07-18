package com.xu.common;

import com.jayway.jsonpath.Predicate;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public
class CommonTest {

    @Test
    public static List<String> TestRandomEmployee() {
        List<String> randomEmployee = new ArrayList<>();

        //随机生成姓名
        String[] names = new String[]{
                "小张","小红","小美","小李","小刘","小郭","小荣","小唐"
        };
        String generate1 = IdNumberGenerator.generate(1);
        int integer = Integer.parseInt(generate1);
        String name = names[integer];
        System.out.println(name);
        randomEmployee.add(name);

        //随机生成用户名
        String suffix = IdNumberGenerator.generate(6);
        System.out.println(name + suffix);
        randomEmployee.add(name + suffix);

        //随机生成手机号
        String[] phonePrefix = new String[]{
                "139","136","131","147","189","185","186","132"
        };
        String phonePrefix1 = phonePrefix[integer];
        String phoneNumber = IdNumberGenerator.generate(8);
        System.out.println(phonePrefix1 + phoneNumber);
        randomEmployee.add(phonePrefix1 + phoneNumber);

        //随机生成身份证号
        String idNumber = IdNumberGenerator.generate(18);
        System.out.println(idNumber);
        randomEmployee.add(idNumber);

        //随机生成性别
        int sexNumber =  integer % 2;
        String[] sexNumbers = new String[]{
                "1","0"
        };
        System.out.println(sexNumbers[sexNumber]);
        randomEmployee.add(sexNumbers[sexNumber]);

        //默认密码是123456
        String defaultPassword = "123456";
        String md5Password = DigestUtils.md5DigestAsHex(defaultPassword.getBytes());
        randomEmployee.add(md5Password);

        return randomEmployee;
    }


    @Test
    void TestOther() {

        List<Integer> integers = new ArrayList<>();
        integers.add(19);
        integers.add(12);
        integers.add(30);
        integers.add(5);
        List<Integer> collect = integers.stream().filter(integer -> (integer > 14)).collect(Collectors.toList());
        System.out.println(collect);
    }
}
