/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eci.analite.data;

import eci.analite.data.service.twitterimpl.TwitterDataExtractor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 *
 * @author user
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class TwitterDataExtractorTest {

    TwitterDataExtractor twde = new TwitterDataExtractor();

    @Test
    public void test_file_gen() {
//        twde.search_data("");
    }
}
