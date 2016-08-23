package com.chat.pk.Upload;

import android.content.Intent;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;


/**
 * Created by //pintu kumar patil 9977638049 india on 5/12/15.
 */
public class MultipartUploadTask extends HttpUploadTask {

    protected static final String PARAM_UTF8_CHARSET = "multipartUtf8Charset";

    private static final String NEW_LINE = "\r\n";
    private static final String TWO_HYPHENS = "--";

    private String boundary;
    private byte[] boundaryBytes;
    private byte[] trailerBytes;
    private boolean isUtf8Charset;

    private final Charset US_ASCII = Charset.forName("US-ASCII");

    @Override
    protected void init(UploadService service, Intent intent) throws IOException {
        super.init(service, intent);
        boundary = getBoundary();
        boundaryBytes = getBoundaryBytes();
        trailerBytes = getTrailerBytes();
        isUtf8Charset = intent.getBooleanExtra(PARAM_UTF8_CHARSET, false);
    }

    @Override
    protected void setupHttpUrlConnection(HttpURLConnection connection) throws IOException {
        if (params.getFiles().size() <= 1) {
            connection.setRequestProperty("Connection", "close");
        } else {
            connection.setRequestProperty("Connection", "Keep-Alive");
        }

        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
    }

    @Override
    protected long getBodyLength() throws UnsupportedEncodingException {
        // get the content length of the entire HTTP/Multipart request body
        long parameterBytes = getRequestParametersLength();
        final long totalFileBytes = getFilesLength();

        return (parameterBytes + totalFileBytes + trailerBytes.length);
    }

    @Override
    protected void writeBody() throws IOException {
        writeRequestParameters();
        writeFiles();
        requestStream.write(trailerBytes, 0, trailerBytes.length);
    }

    private String getBoundary() {
        return "-------AndroidUploadService" + System.currentTimeMillis();
    }

    private byte[] getBoundaryBytes() throws UnsupportedEncodingException {
        return (NEW_LINE + TWO_HYPHENS + boundary + NEW_LINE).getBytes(US_ASCII);
    }

    private byte[] getTrailerBytes() throws UnsupportedEncodingException {
        return (NEW_LINE + TWO_HYPHENS + boundary + TWO_HYPHENS + NEW_LINE).getBytes(US_ASCII);
    }

    private long getFilesLength() throws UnsupportedEncodingException {
        long total = 0;

        for (UploadFile file : params.getFiles()) {
            total += file.getTotalMultipartBytes(boundaryBytes.length, isUtf8Charset);
        }

        return total;
    }

    private long getRequestParametersLength() throws UnsupportedEncodingException {
        long parametersBytes = 0;

        if (!params.getRequestParameters().isEmpty()) {
            for (final NameValue parameter : params.getRequestParameters()) {
                // the bytes needed for every parameter are the sum of the boundary bytes
                // and the bytes occupied by the parameter
                parametersBytes += boundaryBytes.length
                                + parameter.getMultipartBytes(isUtf8Charset).length;
            }
        }

        return parametersBytes;
    }

    private void writeRequestParameters() throws IOException {
        if (!params.getRequestParameters().isEmpty()) {
            for (final NameValue parameter : params.getRequestParameters()) {
                requestStream.write(boundaryBytes, 0, boundaryBytes.length);
                byte[] formItemBytes = parameter.getMultipartBytes(isUtf8Charset);
                requestStream.write(formItemBytes, 0, formItemBytes.length);

                uploadedBodyBytes += boundaryBytes.length + formItemBytes.length;
                broadcastProgress(uploadedBodyBytes, totalBodyBytes);
            }
        }
    }

    private void writeFiles() throws IOException {
        for (UploadFile file : params.getFiles()) {
            if (!shouldContinue)
                break;

            requestStream.write(boundaryBytes, 0, boundaryBytes.length);
            byte[] headerBytes = file.getMultipartHeader(isUtf8Charset);
            requestStream.write(headerBytes, 0, headerBytes.length);

            uploadedBodyBytes += boundaryBytes.length + headerBytes.length;
            broadcastProgress(uploadedBodyBytes, totalBodyBytes);

            final InputStream stream = file.getStream();
            writeStream(stream);
        }
    }

}
