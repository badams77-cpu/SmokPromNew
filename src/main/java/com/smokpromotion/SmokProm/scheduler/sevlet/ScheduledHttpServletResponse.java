package com.smokpromotion.SmokProm.scheduler.sevlet;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This provides a wrapper on a ByteArrayOutputStream but presents as a HttpServletResponse
 * used for getting the content out of a report controller
 */

public class ScheduledHttpServletResponse implements HttpServletResponse {

    private final static String HEAD_KEY_FOR_FILENAME = "Content-Disposition";

    public ByteArrayOutputStream output;

    private String filename = "export.csv";


    public ScheduledHttpServletResponse(){
        output = new ByteArrayOutputStream();
    }

    public byte[] closeAndGet() throws Exception{
        output.close();
        return output.toByteArray();
    }

    public String getFilename() {
        return filename;
    }

    @Override
    public void addCookie(Cookie cookie) {

    }

    @Override
    public boolean containsHeader(String name) {
        return false;
    }

    @Override
    public String encodeURL(String url) {
        return null;
    }

    @Override
    public String encodeRedirectURL(String url) {
        return null;
    }

    @Override
    public String encodeUrl(String url) {
        return null;
    }

    @Override
    public String encodeRedirectUrl(String url) {
        return null;
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {

    }

    @Override
    public void sendError(int sc) throws IOException {

    }

    @Override
    public void sendRedirect(String location) throws IOException {

    }

    @Override
    public void setDateHeader(String name, long date) {

    }

    @Override
    public void addDateHeader(String name, long date) {

    }

    @Override
    public void setHeader(String name, String value) {

        if (name!=null && name.equals(HEAD_KEY_FOR_FILENAME)) {
            Pattern headerPat = Pattern.compile("attachment; filename=\"([a-zA-Z\\.0-9\\-_]+)\"");
            Matcher matcher = headerPat.matcher(value);
            if (matcher.find()) {
                filename = matcher.group(1);
            }
        }
    }

    @Override
    public void addHeader(String name, String value) {

    }

    @Override
    public void setIntHeader(String name, int value) {

    }

    @Override
    public void addIntHeader(String name, int value) {

    }

    @Override
    public void setStatus(int sc) {

    }

    @Override
    public void setStatus(int sc, String sm) {

    }

    @Override
    public int getStatus() {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }



    @Override
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {

            @Override
            public void write(int b) throws IOException {
                output.write(b);
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setWriteListener(WriteListener listener) {

            }
        };
    }

    @Override
    public PrintWriter getWriter() throws IOException {

        return new PrintWriter(output);
    }

    @Override
    public void setCharacterEncoding(String charset) {

    }

    @Override
    public void setContentLength(int len) {

    }

    @Override
    public void setContentLengthLong(long length) {

    }

    @Override
    public void setContentType(String type) {

    }

    @Override
    public void setBufferSize(int size) {

    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {

    }

    @Override
    public void resetBuffer() {

    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {

    }

    @Override
    public void setLocale(Locale loc) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

}
