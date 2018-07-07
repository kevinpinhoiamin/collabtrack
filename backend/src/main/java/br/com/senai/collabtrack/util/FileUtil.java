package br.com.senai.collabtrack.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;

public class FileUtil {

	public void uploadFileSpring(MultipartFile file, String dir, String fileName) {
		try {
			String name = file.getOriginalFilename();
			if(fileName != ""){
				name = fileName;
			}
			
			File folder = new File(dir);
			if (!folder.exists()) {
				folder.mkdirs();
			}

			Path path = Paths.get(dir + "/" +name);
			Files.write(path, file.getBytes());
		 }catch(IOException e){
	     	e.printStackTrace();
		 }
	}
	
	/**
	 * Método que faz o upload de um arquivo
	 * 
	 * @param fileName
	 *            Nome do arquivo
	 * @param inputStream
	 *            Arquivo que será salvo
	 * @param dir
	 *            Diretório de destino
	 * @return Instância do arquivo após o upload
	 */
	public File upload(String fileName, InputStream inputStream, String dir) {
		return upload(fileName, "", inputStream, dir);
	}

	/**
	 * Método que faz o upload de um arquivo
	 * 
	 * @param fileName
	 *            Nome do arquivo
	 * @param newFileName
	 *            Novo nome do arquivo, sem a extensão
	 * @param inputStream
	 *            Arquivo que será salvo
	 * @param dir
	 *            Diretório de destino
	 * @return Instância do arquivo após o upload
	 */
	public File upload(String fileName, String newFileName, InputStream inputStream, String dir) {

		newFileName = newFileName == "" ? UUID.randomUUID().toString() : newFileName;

		// Extensão do arquivo
		String extension = fileName.substring(fileName.lastIndexOf("."));
		// Nome único do arquivo
		String uniqueName = newFileName + extension;
		// Instância do arquivo
		File file = new File(dir, uniqueName);
		// Instância do diretório
		File folder = new File(dir);
		// Se não existir, cria o diretório
		if (!folder.exists()) {
			folder.mkdirs();
		}

		try {
			// OutputStream para escrever o arquivo
			FileOutputStream outputStream = new FileOutputStream(file);
			// Escreve os dados no arquivo
			IOUtils.copy(inputStream, outputStream);
			// Fecha os streams
			inputStream.close();
			outputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// Retorna o arquivo
		return file;
	}

	/**
	 * Método que faz o upload de um arquivo
	 * 
	 * @param fileName
	 *            Nome do arquivo
	 * @param newFileName
	 *            Novo nome do arquivo, sem a extensão
	 * @param base64
	 *            Arquvi no formato String Base64
	 * @param dir
	 *            Diretório de destino
	 * @return Instância do arquivo após o upload
	 */
	public File upload(String fileName, String newFileName, String base64, String dir) {

		// Decode: Converte o Base64 para array de bytes
		byte[] bytes = Base64.getDecoder().decode(base64);
		InputStream in = new ByteArrayInputStream(bytes);

		// Faz o upload do arquivo
		return this.upload(fileName, newFileName, in, dir);

	}

	/**
	 * Método responsável por excluir um arquivo
	 * 
	 * @param file
	 *            Name Nome do arquivo que será excluído
	 * @param dir
	 *            Diretório em que o arquivo se localiza
	 */
	public void deleteFile(String fileName, String dir) {
		File file = new File(dir, fileName);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * Método que busca um arquivo
	 * 
	 * @param fileName
	 *            Nome do arquivo com a extensão
	 * @param dir
	 *            Diretório em que o arquivo está localizado
	 * @return Instância do arquivo encontrado
	 */
	public File getFile(String fileName, String dir) {
		return new File(dir, fileName);
	}

}
