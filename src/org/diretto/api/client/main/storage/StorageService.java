package org.diretto.api.client.main.storage;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import org.diretto.api.client.base.data.UploadInfo;
import org.diretto.api.client.main.storage.download.DownloadProcess;
import org.diretto.api.client.main.storage.download.DownloadReport;
import org.diretto.api.client.main.storage.upload.UploadProcess;
import org.diretto.api.client.main.storage.upload.UploadReport;
import org.diretto.api.client.service.Service;
import org.diretto.api.client.session.UserSession;

/**
 * This interface represents a {@code TaskService}. <br/><br/>
 * 
 * The {@code TaskService} provides the bulk of the platform functionalities in
 * respect of the {@code Storage API}.
 * 
 * @author Tobias Schlecht
 */
public interface StorageService extends Service
{
	/**
	 * Creates an {@link UploadProcess} for the given {@link File} and returns
	 * the {@code UploadProcess} object to trace the upload procedure while the
	 * resource will be uploaded. <br/><br/>
	 * 
	 * <i>Annotation:</i> To start the actual upload process the method
	 * {@link #executeUploadProcess(UploadProcess)} has to be invoked with the
	 * returned {@code UploadProcess} object.
	 * 
	 * @param userSession The corresponding {@code UserSession}
	 * @param uploadInfo The {@code UploadInfo} object
	 * @param file The {@code File} object of the resource
	 * @return The corresponding {@code UploadProcess} object
	 */
	UploadProcess createUploadProcess(UserSession userSession, UploadInfo uploadInfo, File file);

	/**
	 * Creates an {@link UploadProcess} for the given {@link InputStream} and
	 * returns the {@code UploadProcess} object to trace the upload procedure
	 * while the resource will be uploaded. <br/><br/>
	 * 
	 * <i>Annotation:</i> To start the actual upload process the method
	 * {@link #executeUploadProcess(UploadProcess)} has to be invoked with the
	 * returned {@code UploadProcess} object.
	 * 
	 * @param userSession The corresponding {@code UserSession}
	 * @param uploadInfo The {@code UploadInfo} object
	 * @param inputStream The {@code InputStream} of the resource
	 * @return The corresponding {@code UploadProcess} object
	 */
	UploadProcess createUploadProcess(UserSession userSession, UploadInfo uploadInfo, InputStream inputStream);

	/**
	 * Executes the given {@link UploadProcess} and returns an
	 * {@link UploadReport} after the upload procedure has been finished or
	 * {@code null} if the upload procedure was not successful.
	 * 
	 * @param uploadProcess An {@code UploadProcess} object
	 * @return The created {@code UploadReport}
	 */
	UploadReport executeUploadProcess(UploadProcess uploadProcess);

	/**
	 * Creates and executes an {@link UploadProcess} for the given {@link File}.
	 * After the upload procedure has been finished an {@link UploadReport} will
	 * be returned, unless the upload procedure was not successful. If this is
	 * the case {@code null} will be returned. <br/><br/>
	 * 
	 * <i>Attention:</i> Using this method has the consequence that there is no
	 * way to get the created {@code UploadProcess} object and thus there is no
	 * possibility to trace the upload procedure while the resource will be
	 * uploaded either. However, if the upload procedure has to be traced, the
	 * following methods must be used instead: <br/><br/>
	 * 
	 * 1.) {@link #createUploadProcess(UserSession, UploadInfo, File)} <br/> 2.)
	 * {@link #executeUploadProcess(UploadProcess)}
	 * 
	 * @param userSession The corresponding {@code UserSession}
	 * @param uploadInfo The {@code UploadInfo} object
	 * @param file The {@code File} object of the resource
	 * @return The created {@code UploadReport}
	 */
	UploadReport executeUploadProcess(UserSession userSession, UploadInfo uploadInfo, File file);

	/**
	 * Creates and executes an {@link UploadProcess} for the given
	 * {@link InputStream}. After the upload procedure has been finished an
	 * {@link UploadReport} will be returned, unless the upload procedure was
	 * not successful. If this is the case {@code null} will be returned.
	 * <br/><br/>
	 * 
	 * <i>Attention:</i> Using this method has the consequence that there is no
	 * way to get the created {@code UploadProcess} object and thus there is no
	 * possibility to trace the upload procedure while the resource will be
	 * uploaded either. However, if the upload procedure has to be traced, the
	 * following methods must be used instead: <br/><br/>
	 * 
	 * 1.) {@link #createUploadProcess(UserSession, UploadInfo, InputStream)}
	 * <br/> 2.) {@link #executeUploadProcess(UploadProcess)}
	 * 
	 * @param userSession The corresponding {@code UserSession}
	 * @param uploadInfo The {@code UploadInfo} object
	 * @param inputStream The {@code InputStream} of the resource
	 * @return The created {@code UploadReport}
	 */
	UploadReport executeUploadProcess(UserSession userSession, UploadInfo uploadInfo, InputStream inputStream);

	/**
	 * Creates a {@link DownloadProcess} for the resource of the given file
	 * {@link URL} and returns the {@code DownloadProcess} object to trace the
	 * download procedure while the resource will be downloaded. The content of
	 * the requested resource will be written to the given {@link OutputStream}.
	 * <br/><br/>
	 * 
	 * <i>Annotation:</i> To start the actual download process the method
	 * {@link #executeDownloadProcess(DownloadProcess)} has to be invoked with
	 * the returned {@code DownloadProcess} object.
	 * 
	 * @param fileURL The {@code URL} of the resource to be downloaded
	 * @param outputStream The {@code OutputStream} to which the resource
	 *        content should be written
	 * @return The corresponding {@code DownloadProcess} object
	 */
	DownloadProcess createDownloadProcess(URL fileURL, OutputStream outputStream);

	/**
	 * Executes the given {@link DownloadReport} and returns an
	 * {@link DownloadProcess} after the download procedure has been finished or
	 * {@code null} if the download procedure was not successful.
	 * 
	 * @param downloadProcess An {@code DownloadProcess} object
	 * @return The created {@code DownloadReport}
	 */
	DownloadReport executeDownloadProcess(DownloadProcess downloadProcess);

	/**
	 * Creates and executes a {@link DownloadProcess} for the resource of the
	 * given file {@link URL}. The content of the requested resource will be
	 * written to the given {@link OutputStream}. After the download procedure
	 * has been finished an {@link DownloadReport} will be returned, unless the
	 * download procedure was not successful. If this is the case {@code null}
	 * will be returned. <br/><br/>
	 * 
	 * <i>Attention:</i> Using this method has the consequence that there is no
	 * way to get the created {@code DownloadProcess} object and thus there is
	 * no possibility to trace the download procedure while the resource will be
	 * downloaded either. However, if the download procedure has to be traced,
	 * the following methods must be used instead: <br/><br/>
	 * 
	 * 1.) {@link #createDownloadProcess(URL, OutputStream)} <br/> 2.)
	 * {@link #executeDownloadProcess(DownloadProcess)}
	 * 
	 * @param fileURL The {@code URL} of the resource to be downloaded
	 * @param outputStream The {@code OutputStream} to which the resource
	 *        content should be written
	 * @return The created {@code DownloadReport}
	 */
	DownloadReport executeDownloadProcess(URL fileURL, OutputStream outputStream);
}
