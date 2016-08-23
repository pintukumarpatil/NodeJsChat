package com.chat.pk.Upload;

import android.content.Context;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by //pintu kumar patil 9977638049 india on 30/11/15.
 */
public class BinaryUploadRequest extends HttpUploadRequest {

    public BinaryUploadRequest(final Context context, final String uploadId, final String serverUrl) {
        super(context, uploadId, serverUrl);
    }


    public BinaryUploadRequest(final Context context, final String serverUrl) {
        this(context, null, serverUrl);
    }

    @Override
    protected Class getTaskClass() {
        return BinaryUploadTask.class;
    }

    public BinaryUploadRequest setFileToUpload(String path) throws FileNotFoundException {
        params.getFiles().clear();
        params.addFile(new UploadFile(path));
        return this;
    }

    @Override
    public BinaryUploadRequest setNotificationConfig(UploadNotificationConfig config) {
        super.setNotificationConfig(config);
        return this;
    }

    @Override
    public BinaryUploadRequest setAutoDeleteFilesAfterSuccessfulUpload(boolean autoDeleteFiles) {
        super.setAutoDeleteFilesAfterSuccessfulUpload(autoDeleteFiles);
        return this;
    }

    @Override
    public BinaryUploadRequest addHeader(String headerName, String headerValue) {
        super.addHeader(headerName, headerValue);
        return this;
    }

    @Override
    public BinaryUploadRequest setBasicAuth(final String username, final String password) {
        super.setBasicAuth(username, password);
        return this;
    }

    @Override
    public BinaryUploadRequest setMethod(String method) {
        super.setMethod(method);
        return this;
    }

    @Override
    public BinaryUploadRequest setCustomUserAgent(String customUserAgent) {
        super.setCustomUserAgent(customUserAgent);
        return this;
    }

    @Override
    public BinaryUploadRequest setMaxRetries(int maxRetries) {
        super.setMaxRetries(maxRetries);
        return this;
    }

    @Override
    public BinaryUploadRequest setUsesFixedLengthStreamingMode(boolean fixedLength) {
        super.setUsesFixedLengthStreamingMode(fixedLength);
        return this;
    }

    @Override
    public HttpUploadRequest addParameter(String paramName, String paramValue) {
        logDoesNotSupportParameters();
        return this;
    }

    @Override
    public HttpUploadRequest addArrayParameter(String paramName, String... array) {
        logDoesNotSupportParameters();
        return this;
    }

    @Override
    public HttpUploadRequest addArrayParameter(String paramName, List<String> list) {
        logDoesNotSupportParameters();
        return this;
    }

    private void logDoesNotSupportParameters() {
        Logger.error(getClass().getSimpleName(),
                     "This upload method does not support adding parameters");
    }
}
