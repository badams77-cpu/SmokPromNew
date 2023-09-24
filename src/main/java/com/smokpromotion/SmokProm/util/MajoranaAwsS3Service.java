package com.smokpromotion.SmokProm.util;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MajoranaAwsS3Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(MajoranaAwsS3Service.class);

    private final Map<MajoranaAwsAccessCredentials, AmazonS3> s3map;

    private Regions region;


    @Autowired
    public MajoranaAwsS3Service(@Value("${Majorana_AWS_REGION:UNKNOWN_REGION}") String awsRegion){
        region = Regions.fromName(awsRegion);
        s3map = new HashMap<>();

    }


    private synchronized AmazonS3 getS3(MajoranaAwsAccessCredentials cred){
        AmazonS3 s3 = s3map.get(cred);
        if (s3==null){
            AWSCredentials credentials = new BasicAWSCredentials(cred.getAccessKey(), cred.getSecretKey());
            s3 = AmazonS3ClientBuilder.standard()
                    .withCredentials(new AWSStaticCredentialsProvider(credentials))
                    .withRegion(region).build();
            s3map.put(cred, s3);
        }
        return s3;
    }

    private File convertMultipartFileToS3File(MultipartFile file, int practiceGroupId, int practiceId, String key) throws IOException
    {
        File convFile = File.createTempFile(practiceGroupId+"_"+key, ".tmp"); // choose your own extension I guess? Filename accessible with convFile.getAbsolutePath()
        FileOutputStream fos = new FileOutputStream(convFile);
        S3File s3file = new S3File();
        s3file.setPracticeGroupId(practiceGroupId);
        s3file.setPracticeId(practiceId);
        s3file.setData(file.getBytes());
        s3file.writeTo(fos);
        fos.close();

        return convFile;
    }

    public void upload(MajoranaAwsAccessCredentials cred, MajoranaAwsS3Location loc, int practiceGroupId, int practiceId, String key, MultipartFile file){
        File temp=null;
        try {
            temp = convertMultipartFileToS3File(file, practiceGroupId, practiceId, key);
            String wholeKey = loc.getPrefix() +"/"+practiceGroupId+"/"+key;
            getS3(cred).putObject(loc.getBucket(), wholeKey, temp);
        } catch (AmazonServiceException e) {
            LOGGER.warn("upload AWS Exception ",e);
        } catch ( IOException e){
            LOGGER.warn("upload IOException ",e);
        } finally {
            if (temp!=null){
                temp.delete();
            }
        }
    }

    public void delete(MajoranaAwsAccessCredentials cred, MajoranaAwsS3Location loc,int practiceGroupId, String key){
        String wholeKey = loc.getPrefix() +"/"+practiceGroupId+"/"+key;
        try {
            getS3(cred).deleteObject(loc.getBucket(), wholeKey);
        } catch (AmazonServiceException e) {
            LOGGER.warn("delete AWS Exception ",e);
        }
    }

    public void deleteForGroupBeforeDate(MajoranaAwsAccessCredentials cred, MajoranaAwsS3Location loc,int practiceGroupId, Date date){
        ListObjectsV2Result result = getS3(cred).listObjectsV2(loc.getBucket());
        List<S3ObjectSummary> objects = result.getObjectSummaries();
        List<S3ObjectSummary> matches = objects.stream().filter(x->x.getKey().startsWith(loc.getPrefix() +"/"+practiceGroupId) && x.getLastModified().before(date))
                .collect(Collectors.toList());
        matches.forEach(m-> {
            try {
                getS3(cred).deleteObject(loc.getBucket(), m.getKey());
            } catch (AmazonServiceException e) {
                LOGGER.warn("deleteForGroupAfterDate delete:  "+m.getKey()+ " AWS Exception ", e);
            }
        });
    }

    public void writeObjectToStream(MajoranaAwsAccessCredentials cred, MajoranaAwsS3Location loc,int practiceGroupId, int practiceId, String key, OutputStream fos){
        String wholeKey = loc.getPrefix() +"/"+practiceGroupId+"/"+key;
        try {
            S3Object o = getS3(cred).getObject(loc.getBucket(), wholeKey);
            S3ObjectInputStream s3is = o.getObjectContent();
            S3File s3file = S3File.readFully(s3is);
            if (s3file.getPracticeGroupId()!=practiceGroupId){
                LOGGER.warn("writeObjectToFOS: bad practice group id ");
                return;
            }
            ByteArrayInputStream bis = s3file.getByteArrayInputStream();
            byte[] read_buf = new byte[1024];
            int read_len = 0;
            while ((read_len = bis.read(read_buf)) > 0) {
                fos.write(read_buf, 0, read_len);
            }
            s3is.close();
            fos.close();
        } catch (AmazonServiceException e) {
            LOGGER.warn("writeObjectToFOS AWS Exception ",e);
        } catch (FileNotFoundException e) {
            LOGGER.warn("writeObjectToFOS FOS Exception ",e);
        } catch (IOException e) {
            LOGGER.warn("writeObjectToFOS IOException ",e);
        }
    }

    public S3File getS3File(MajoranaAwsAccessCredentials cred, MajoranaAwsS3Location loc,int practiceGroupId, int practiceId, String key){
        String wholeKey = loc.getPrefix() +"/"+practiceGroupId+"/"+key;
        try {
            S3Object o = getS3(cred).getObject(loc.getBucket(), wholeKey);
            S3ObjectInputStream s3is = o.getObjectContent();
            S3File s3file = S3File.readFully(s3is);
            s3is.close();
            return s3file;
        } catch(AmazonServiceException e){
                LOGGER.warn("getS3File: AWS Exception ", e);
        } catch(FileNotFoundException e){
                LOGGER.warn("getS3File: FileNotFoundException Exception ", e);
        } catch(IOException e){
                LOGGER.warn("getS3File: IOException ", e);
        }
        return null;
    }



}
