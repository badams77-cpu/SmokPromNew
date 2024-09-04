package com.smokpromotion.SmokProm.scheduler;

import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.model.enums.AesKeyStrength;
import net.lingala.zip4j.model.enums.CompressionLevel;
import net.lingala.zip4j.model.enums.CompressionMethod;
import net.lingala.zip4j.model.enums.EncryptionMethod;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

@Service
public class ZipHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(ZipHelper.class);

    public  byte[] zipFileProtected(byte[] fileBytes, String fileName, String pass) throws IOException {
        return zipFileProtected(fileBytes, fileName, pass, false);
    }

    public  byte[] zipFileProtected(byte[] fileBytes, String fileName, String pass, boolean useAES) throws IOException {

        ByteArrayInputStream inputByteStream = null;
        ByteArrayOutputStream outputByteStream = null;
        net.lingala.zip4j.io.outputstream.ZipOutputStream outputZipStream = null;

        try {
            //write the zip bytes to a byte array
            outputByteStream = new ByteArrayOutputStream();
            outputZipStream = new net.lingala.zip4j.io.outputstream.ZipOutputStream(outputByteStream, pass.toCharArray());

            //input byte stream to read the input bytes
            inputByteStream = new ByteArrayInputStream(fileBytes);

            //init the zip parameters
            ZipParameters zipParams = new ZipParameters();
            zipParams.setCompressionMethod(CompressionMethod.DEFLATE);
            zipParams.setCompressionLevel(CompressionLevel.HIGHER);
            zipParams.setEncryptFiles(true);
            if (useAES) {
                zipParams.setEncryptionMethod(EncryptionMethod.AES);
                zipParams.setAesKeyStrength(AesKeyStrength.KEY_STRENGTH_256);
            } else {
                zipParams.setEncryptionMethod(EncryptionMethod.ZIP_STANDARD);
            }
 //           zipParams.setSourceExternalStream(true);
            zipParams.setFileNameInZip(fileName);

            //create zip entry
            outputZipStream.putNextEntry( zipParams);
            IOUtils.copy(inputByteStream, outputZipStream);
            outputZipStream.closeEntry();

            //finish up
//            outputZipStream.finish();

            IOUtils.closeQuietly(inputByteStream);
            IOUtils.closeQuietly(outputByteStream);
            IOUtils.closeQuietly(outputZipStream);

            return outputByteStream.toByteArray();

        } catch (ZipException e) {
            LOGGER.error("Error zip file",e);
        } finally {
            IOUtils.closeQuietly(inputByteStream);
            IOUtils.closeQuietly(outputByteStream);
            IOUtils.closeQuietly(outputZipStream);
        }
        return null;
    }
}
