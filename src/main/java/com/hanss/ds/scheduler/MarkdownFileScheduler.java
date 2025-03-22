package com.hanss.ds.scheduler;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;
import com.github.sardine.DavResource;
import com.hanss.ds.config.AppConfig;
import com.hanss.ds.service.DocumentService;
import com.hanss.ds.utils.Const;
import com.hanss.ds.utils.UrlEncoder;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MarkdownFileScheduler {
    private final AppConfig appConfig;
    private final DocumentService documentService;
    private final Sardine sardine;


    public MarkdownFileScheduler(AppConfig appConfig, DocumentService documentService) {
        this.appConfig = appConfig;
        this.documentService = documentService;
        this.sardine = SardineFactory.begin(this.appConfig.getWebdavUsername(), this.appConfig.getWebdavPassword());
    }

    @Scheduled(cron = "0 0 * * * ?", zone = "Europe/Moscow") // Run every hour
    public void fetchAndProcessMarkdownFiles() throws IOException, NoSuchAlgorithmException {
        this.processDirectory(this.appConfig.getWebdavServerUrl()+ "/" + Const.OBSIDIAN);
    }

    private void processDirectory(String url) throws IOException, NoSuchAlgorithmException {
        // Получаем список файлов
        List<DavResource> resources = sardine.list(url);
        for (int i=1; i<resources.size(); i++) {
            DavResource resource = resources.get(i);
            String name = UrlEncoder.encodeNonPrintableCharacters(resource.getName());
            if (resource.isDirectory() && !resource.getName().equals("/"+ Const.OBSIDIAN)) {
                // Это директория, рекурсивно обрабатываем её
                processDirectory(url+ "/" + name);
            } else if (resource.getName().endsWith(".md")) {
                InputStream stream = sardine.get(url + "/" + name);
                Map<String, String> metadata = new java.util.HashMap<>();
                if (resource.getName()!=null) metadata.put(Const.FILENAME, resource.getName());
                if (resource.getPath()!=null) metadata.put(Const.PATH, resource.getPath());
                if (resource.getCreation()!=null) metadata.put(Const.CREATION, resource.getCreation().toString());
                if (resource.getModified()!=null) metadata.put(Const.MODIFIED, resource.getModified().toString());
                String content = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
                documentService.saveDocument(content, metadata);
            }
        }
    }
}
