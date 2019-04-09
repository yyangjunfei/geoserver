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
        //GeoServer的连接配置
        String url = "http://localhost:8080/geoserver";
        String username = "admin";
        String passwd = "geoserver";
        //Postgis连接配置
        String postgisHost = "localhost";
        int postgisPort = 5432;
        String postgisUser = "postgres";
        String postgisPassword = "888888";
        String postgisDatabase = "postgis_23_sample";
        //判断工作区是否存在，不存在创建
        URL u = new URL(url);
        GeoServerRESTManager manager = new GeoServerRESTManager(u, username, passwd);
        GeoServerRESTPublisher publisher = manager.getPublisher();
        List<String> workspaces = manager.getReader().getWorkspaceNames();
        if (!workspaces.contains(workname)) {
            boolean createws = publisher.createWorkspace(workname);
            System.out.println("创建工作区成功! : " + createws);
        } else {
            System.out.println("工作区已经存在! :" + workname);
        }
        //判断数据存储是否存在，不存在创建
        RESTDataStore restStore = manager.getReader().getDatastore(workname, storename);
        if (restStore == null) {
            GSPostGISDatastoreEncoder store = new GSPostGISDatastoreEncoder(storename);
            store.setHost(postgisHost);//url
            store.setPort(postgisPort);//端口
            store.setUser(postgisUser);// 数据库用户名
            store.setPassword(postgisPassword);// 数据库密码
            store.setDatabase(postgisDatabase);// 数据库类型
            store.setSchema("public"); //公共public
            store.setConnectionTimeout(20);// 超时设置
            store.setMaxConnections(20); // 最大连接数
            store.setMinConnections(1);   // 最小连接数
            store.setExposePrimaryKeys(true);
            boolean createStore = manager.getStoreManager().create(workname, store);
            System.out.println("创建数据存储成功! : " + createStore);
        } else {
            System.out.println("数据存储已经存在!:" + storename);
        }
        //判断图层是否存在，不存在创建并发布
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