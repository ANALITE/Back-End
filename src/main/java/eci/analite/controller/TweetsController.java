/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eci.analite.controller;

import com.mongodb.client.gridfs.model.GridFSFile;
import eci.analite.data.service.twitterimpl.TwitterDataExtractor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 *
 * @author user
 */
@RestController
@RequestMapping("twitter")
public class TweetsController {

    @Autowired
    GridFsTemplate gridFsTemplate;

    TwitterDataExtractor twde = new TwitterDataExtractor();

    @CrossOrigin("*")
    @RequestMapping("/query/{filename}")
    public ResponseEntity<InputStreamResource> getFileByQuery(@PathVariable String filename) {
        try {
            File query = twde.search_data(filename);
            return ResponseEntity.ok().contentType(new MediaType("text", "csv", Charset.forName("utf-8"))).body(new InputStreamResource(new FileInputStream(query)));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(TweetsController.class.getName()).log(Level.SEVERE, null, ex);
            return new ResponseEntity(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @CrossOrigin("*")
    @RequestMapping("/data/{filename}")
    public ResponseEntity<InputStreamResource> getQueryFileMongo(@PathVariable String filename) {
        GridFSFile file = gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("filename").is(filename)));
        if (file != null) {
            try {
                GridFsResource resource = gridFsTemplate.getResource(file.getFilename());
                return ResponseEntity.ok()
                        .contentType(MediaType.valueOf(resource.getContentType()))
                        .body(new InputStreamResource(resource.getInputStream()));
            } catch (IOException e) {
                return new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @CrossOrigin("*")
    @PostMapping("/data")
    public String handleFileUpload(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        try {
            gridFsTemplate.store(file.getInputStream(), file.getOriginalFilename(), file.getContentType());
            return String.format("/%s", file.getOriginalFilename());
        } catch (IOException e) {
            return HttpStatus.INTERNAL_SERVER_ERROR.toString();
        }
    }
}
