package dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoClientOptions.Builder;
import com.mongodb.WriteConcern;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.Filters;
import static com.mongodb.client.model.Filters.*;  
import static com.mongodb.client.model.Projections.*;  
import static com.mongodb.client.model.Sorts.*;
import static com.mongodb.client.model.Accumulators.*;  
import static com.mongodb.client.model.Aggregates.*; 
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.model.Updates.*;
import com.mongodb.client.result.DeleteResult;


public enum MongoUtil {
     /**
     * ����һ��ö�ٵ�Ԫ�أ�����������һ��ʵ��
     */
    instance;

    private static MongoClient mongoClient;

    static {
        System.out.println("===============MongoDBUtil��ʼ��========================");
        String ip = "localhost";
        int port =27017;
        instance.mongoClient = new MongoClient(ip, port);
        // �󲿷��û�ʹ��mongodb���ڰ�ȫ�����£��������mongodb��Ϊ��ȫ��֤ģʽ������Ҫ�ڿͻ����ṩ�û��������룺
        // boolean auth = db.authenticate(myUserName, myPassword);
        Builder options = new MongoClientOptions.Builder();
        options.cursorFinalizerEnabled(true);
        // options.autoConnectRetry(true);// �Զ�����true
        // options.maxAutoConnectRetryTime(10); // the maximum auto connect retry time
        options.connectionsPerHost(300);// ���ӳ�����Ϊ300������,Ĭ��Ϊ100
        options.connectTimeout(30000);// ���ӳ�ʱ���Ƽ�>3000����
        options.maxWaitTime(5000); //
        options.socketTimeout(0);// �׽��ֳ�ʱʱ�䣬0������
        options.threadsAllowedToBlockForConnectionMultiplier(5000);// �̶߳���������������߳������˶��оͻ��׳���Out of semaphores to get db������
        options.writeConcern(WriteConcern.SAFE);//
        options.build();
    }

    // ------------------------------------���÷���---------------------------------------------------
    /**
     * ��ȡDBʵ�� - ָ��DB
     * 
     * @param dbName
     * @return
     */
    public MongoDatabase getDB(String dbName) {
        if (dbName != null && !"".equals(dbName)) {
            MongoDatabase database = mongoClient.getDatabase(dbName);
            return database;
        }
        return null;
    }

    /**
     * ��ȡcollection���� - ָ��Collection
     * 
     * @param collName
     * @return
     */
    public MongoCollection<Document> getCollection(String dbName, String collName) {
        if (null == collName || "".equals(collName)) {
            return null;
        }
        if (null == dbName || "".equals(dbName)) {
            return null;
        }
        MongoCollection<Document> collection = mongoClient.getDatabase(dbName).getCollection(collName);
        return collection;
    }

    /**
     * ��ѯDB�µ����б���
     */
    public List<String> getAllCollections(String dbName) {
        MongoIterable<String> colls = getDB(dbName).listCollectionNames();
        List<String> _list = new ArrayList<String>();
        for (String s : colls) {
            _list.add(s);
        }
        return _list;
    }

    /**
     * ��ȡ�������ݿ������б�
     * 
     * @return
     */
    public MongoIterable<String> getAllDBNames() {
        MongoIterable<String> s = mongoClient.listDatabaseNames();
        return s;
    }

    /**
     * ɾ��һ�����ݿ�
     */
    public void dropDB(String dbName) {
        getDB(dbName).drop();
    }

    /**
     * ���Ҷ��� - ��������_id
     * 
     * @param collection
     * @param id
     * @return
     */
    public Document findById(MongoCollection<Document> coll, String id) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Document myDoc = coll.find(Filters.eq("_id", _idobj)).first();
        return myDoc;
    }

    /** ͳ���� */
    public int getCount(MongoCollection<Document> coll) {
        int count = (int) coll.count();
        return count;
    }

    /** ������ѯ */
    public MongoCursor<Document> find(MongoCollection<Document> coll, Bson filter) {
        return coll.find(filter).iterator();
    }

    /** ��ҳ��ѯ */
    public MongoCursor<Document> findByPage(MongoCollection<Document> coll, Bson filter, int pageNo, int pageSize) {
        Bson orderBy = new BasicDBObject("_id", 1);
        return coll.find(filter).sort(orderBy).skip((pageNo - 1) * pageSize).limit(pageSize).iterator();
    }
    

    /**
     * ͨ��IDɾ��
     * 
     * @param coll
     * @param id
     * @return
     */
    public int deleteById(MongoCollection<Document> coll, String id) {
        int count = 0;
        ObjectId _id = null;
        try {
            _id = new ObjectId(id);
        } catch (Exception e) {
            return 0;
        }
        Bson filter = Filters.eq("_id", _id);
        DeleteResult deleteResult = coll.deleteOne(filter);
        count = (int) deleteResult.getDeletedCount();
        return count;
    }

    /**
     * FIXME
     * 
     * @param coll
     * @param id
     * @param newdoc
     * @return
     */
    public Document updateById(MongoCollection<Document> coll, String id, Document newdoc) {
        ObjectId _idobj = null;
        try {
            _idobj = new ObjectId(id);
        } catch (Exception e) {
            return null;
        }
        Bson filter = Filters.eq("_id", _idobj);
        // coll.replaceOne(filter, newdoc); // ��ȫ���
        coll.updateOne(filter, new Document("$set", newdoc));
        return newdoc;
    }

    public void dropCollection(String dbName, String collName) {
        getDB(dbName).getCollection(collName).drop();
    }

    /**
     * �ر�Mongodb
     */
    public void close() {
        if (mongoClient != null) {
            mongoClient.close();
            mongoClient = null;
        }
    }

    /**
     * �������
     * 
     * @param args
     * @throws CloneNotSupportedException 
     */
    public static void main(String[] args) {
        
        String dbName = "test";
        String collName = "wd_paper_scie";
        MongoCollection<Document> coll = MongoUtil.instance.getCollection(dbName, collName);
        //coll.createIndex(new Document("validata",1));//��������
        //coll.createIndex(new Document("id",1));
       // coll.createIndex(new Document("ut_wos",1),new IndexOptions().unique(true));//����Ψһ����
        //coll.dropIndexes();//ɾ������
        //coll.dropIndex("validata_1");//����������ɾ��ĳ������
        ListIndexesIterable<Document> list = coll.listIndexes();//��ѯ��������
        for (Document document : list) {
            System.out.println(document.toJson());
        }
        coll.find(Filters.and(Filters.eq("x", 1), Filters.lt("y", 3)));
        coll.find(and(eq("x", 1), lt("y", 3)));
        coll.find().sort(ascending("title"));  
        coll.find().sort(new Document("id",1)); 
        coll.find(new Document("$or", Arrays.asList(new Document("owner", "tom"), new Document("words", new Document("$gt", 350)))));
        coll.find().projection(fields(include("title", "owner"), excludeId()));  
        // coll.updateMany(Filters.eq("validata", 1), Updates.set("validata", 0));
        //coll.updateMany(Filters.eq("validata", 1), new Document("$unset", new Document("jigou", "")));//ɾ��ĳ���ֶ�
        //coll.updateMany(Filters.eq("validata", 1), new Document("$rename", new Document("affiliation", "affiliation_full")));//�޸�ĳ���ֶ���
        //coll.updateMany(Filters.eq("validata", 1), new Document("$rename", new Document("affiliationMeta", "affiliation")));
        //coll.updateMany(Filters.eq("validata", 1), new Document("$set", new Document("validata", 0)));//�޸��ֶ�ֵ
//        MongoCursor<Document> cursor1 =coll.find(Filters.eq("ut_wos", "WOS:000382970200003")).iterator();
//        while(cursor1.hasNext()){
//            Document sd=cursor1.next();
//            System.out.println(sd.toJson());
//            coll.insertOne(sd);
//        }
       
//        MongoCursor<Document> cursor1 =coll.find(Filters.elemMatch("affInfo", Filters.eq("firstorg", 1))).iterator();
//        while(cursor1.hasNext()){
//            Document sd=cursor1.next();
//            System.out.println(sd.toJson());
//        }
        //��ѯֻ����ָ���ֶ�
       // MongoCursor<Document> doc= coll.find().projection(Projections.fields(Projections.include("ut_wos","affiliation"),Projections.excludeId())).iterator();
        //"ut_wos" : "WOS:000382970200003"
       //coll.updateMany(Filters.eq("validata", 1), new Document("$set", new Document("validata", 0)));
        //coll.updateMany(Filters.eq("validata", 0), new Document("$rename", new Document("sid", "ssid")), new UpdateOptions().upsert(false));
        //coll.updateOne(Filters.eq("ut_wos", "WOS:000382970200003"), new Document("$set", new Document("validata", 0)));
        //long isd=coll.count(Filters.elemMatch("affInfo", Filters.elemMatch("affiliationJGList", Filters.eq("sid", 0))));
       // System.out.println(isd);
        //MongoCursor<Document> doc= coll.find(Filters.elemMatch("affInfo", Filters.elemMatch("affiliationJGList", Filters.ne("sid", 0)))).projection(Projections.fields(Projections.elemMatch("affInfo"),Projections.excludeId())).iterator();
//       MongoCursor<Document> doc= coll.find().projection(Projections.include("affInfo","ssid")).iterator();
//       while(doc.hasNext()){
//            Document sd=doc.next();
//            System.out.println(sd.toJson());
//            
//        }
        MongoUtil.instance.close();
        // �������
//         for (int i = 1; i <= 4; i++) {
//         Document doc = new Document();
//         doc.put("name", "zhoulf");
//         doc.put("school", "NEFU" + i);
//         Document interests = new Document();
//         interests.put("game", "game" + i);
//         interests.put("ball", "ball" + i);
//         doc.put("interests", interests);
//         coll.insertOne(doc);
//         }
//       
       /* MongoCursor<Document> sd =coll.find().iterator();
        while(sd.hasNext()){
            Document doc = sd.next();
            String id =  doc.get("_id").toString();
            List<AffiliationJG> list = new ArrayList<AffiliationJG>();
            AffiliationJG jg = new AffiliationJG();
            jg.setAddress("123");
            jg.setCid(2);
            jg.setCname("eeee");
            jg.setSid(3);
            jg.setSname("rrrr");
            AffiliationJG jg2 = new AffiliationJG();
            jg2.setAddress("3242");
            jg2.setCid(2);
            jg2.setCname("ers");
            jg2.setSid(3);
            jg2.setSname("rasdr");
            list.add(jg);
            list.add(jg2);
            AffiliationList af = new AffiliationList();
            af.setAffiliationAuthos("33333");
            af.setAffiliationinfo("asdsa");
            af.setAffiliationJGList(list);
            JSONObject json = JSONObject.fromObject(af);
            doc.put("affInfo", json);
            MongoDBUtil.instance.updateById(coll, id, doc);
        }*/
        
//        Bson bs = Filters.eq("name", "zhoulf");
//        coll.find(bs).iterator();
        // // ����ID��ѯ
        // String id = "556925f34711371df0ddfd4b";
        // Document doc = MongoDBUtil2.instance.findById(coll, id);
        // System.out.println(doc);

        // ��ѯ���
        // MongoCursor<Document> cursor1 = coll.find(Filters.eq("name", "zhoulf")).iterator();
        // while (cursor1.hasNext()) {
        // org.bson.Document _doc = (Document) cursor1.next();
        // System.out.println(_doc.toString());
        // }
        // cursor1.close();

        // ��ѯ���
//         MongoCursor<WdPaper> cursor2 = coll.find(WdPaper.class).iterator();
//         while(cursor2.hasNext()){
//             WdPaper doc = cursor2.next();
//             System.out.println(doc.getUt_wos());
//         }
        // ɾ�����ݿ�
        // MongoDBUtil2.instance.dropDB("testdb");

        // ɾ����
        // MongoDBUtil2.instance.dropCollection(dbName, collName);

        // �޸�����
        // String id = "556949504711371c60601b5a";
        // Document newdoc = new Document();
        // newdoc.put("name", "ʱ��");
        // MongoDBUtil.instance.updateById(coll, id, newdoc);

        // ͳ�Ʊ�
         //System.out.println(MongoDBUtil.instance.getCount(coll));

        // ��ѯ����
//        Bson filter = Filters.eq("count", 0);
//        MongoDBUtil.instance.find(coll, filter);

    }

}