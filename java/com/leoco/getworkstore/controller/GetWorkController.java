package com.leoco.getworkstore.controller;
import it.geosolutions.geoserver.rest.GeoServerRESTManager;
import it.geosolutions.geoserver.rest.GeoServerRESTPublisher;
import it.geosolutions.geoserver.rest.decoder.RESTDataStore;
import it.geosolutions.geoserver.rest.decoder.RESTLayer;
import it.geosolutions.geoserver.rest.encoder.GSLayerEncoder;
import it.geosolutions.geoserver.rest.encoder.datastore.GSPostGISDatastoreEncoder;
import it.geosolutions.geoserver.rest.encoder.feature.GSFeatureTypeEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import javax.servlet.http.HttpServletRequest;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
@Controller
@RequestMapping(value = {"/leoco"})
public class GetWorkController {
    @RequestMapping(value = {"/getwork"})
    public String geoserver(Model model) {

        return "getworkstore";
    }
    @RequestMapping(value = {"/getworkstore"})
    public String getwork(HttpServletRequest request) throws MalformedURLException {
        String workname = request.getParameter("workname");
        String storename = request.getParameter("storename");
        String tablename = request.getParameter("tablename");
        String url = "http://localhost:8080/geoserver";
        String username = "admin";
        String passwd = "geoserver";
        String postgisHost = "localhost";
        int postgisPort = 5432;
        String postgisUser = "postgres";
        String postgisPassword = "";
        String postgisDatabase = "";
        URL u = new URL(url);
        GeoServerRESTManager manager = new GeoServerRESTManager(u, username, passwd);
        GeoServerRESTPublisher publisher = manager.getPublisher();
        List<String> workspaces = manager.getReader().getWorkspaceNames();
        if (!workspaces.contains(workname)) {
            boolean createws = publisher.createWorkspace(workname);
            System.out.println("创建成功! : " + createws);
        } else {
            System.out.println("已经存在! :" + workname);
        }
        RESTDataStore restStore = manager.getReader().getDatastore(workname, storename);
        if (restStore == null) {
            GSPostGISDatastoreEncoder store = new GSPostGISDatastoreEncoder(storename);
            store.setHost(postgisHost);
            store.setPort(postgisPort);
            store.setUser(postgisUser);
            store.setPassword(postgisPassword);
            store.setDatabase(postgisDatabase);
            store.setSchema("public"); 
            store.setConnectionTimeout(20);
            store.setMaxConnections(20);
            store.setMinConnections(1);
            store.setExposePrimaryKeys(true);
            boolean createStore = manager.getStoreManager().create(workname, store);
            System.out.println("创建成功! : " + createStore);
        } else {
            System.out.println("已经存在!:" + storename);
        }
        RESTLayer layer = manager.getReader().getLayer(workname, tablename);
        if (layer == null) {
            GSFeatureTypeEncoder pds = new GSFeatureTypeEncoder();
            pds.setTitle(tablename);
            pds.setName(tablename);
            pds.setSRS("EPSG:4326");
            GSLayerEncoder layerEncoder = new GSLayerEncoder();
            boolean publish = manager.getPublisher().publishDBLayer(workname, storename, pds, layerEncoder);
            System.out.println("图层创建: " + publish);
            return "successhtml";
        } else {
            System.out.println("图层创建已经发布:" + tablename);
            return "errorhtml";
        }
    }
 }
