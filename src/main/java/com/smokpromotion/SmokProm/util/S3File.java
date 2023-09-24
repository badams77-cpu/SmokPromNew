package com.smokpromotion.SmokProm.util;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class S3File {

    private static final Logger LOGGER = LoggerFactory.getLogger(S3File.class);

    private int practiceGroupId;
    private int practiceId;


    private byte[] data;

    public S3File(){

    }

    public S3File(int practiceGroupId, int practiceId, byte[] data){
        this.practiceGroupId = practiceGroupId;
        this.practiceId = practiceId;
        this.data = data;
    }

    public static S3File readFully(InputStream is) throws IOException {
        ObjectInputStream ois = new ObjectInputStream(is);
        S3File out = new S3File();
        out.practiceGroupId = ois.readInt();
        out.practiceId = ois.readInt();
        int len = ois.readInt();
        out.data = new byte[len];
        IOUtils.readFully(ois,out.data);
        ois.close();
        return out;
    }

    public void writeTo(OutputStream os) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(os);
        oos.writeInt(practiceGroupId);
        oos.writeInt(practiceId);
        oos.writeInt(data.length);
        oos.write(data);
        oos.close();
    }

    public void writeDataTo(OutputStream os) throws IOException {
        os.write(data);
    }

    public ByteArrayInputStream getByteArrayInputStream(){
        return new ByteArrayInputStream(data);
    }

    public int getPracticeGroupId() {
        return practiceGroupId;
    }

    public void setPracticeGroupId(int practiceGroupId) {
        this.practiceGroupId = practiceGroupId;
    }

    public int getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(int practiceId) {
        this.practiceId = practiceId;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "S3File{" +
                "practiceGroupId=" + practiceGroupId +
                ", practiceId=" + practiceId +
                ", data='" + data + '\'' +
                '}';
    }
}
