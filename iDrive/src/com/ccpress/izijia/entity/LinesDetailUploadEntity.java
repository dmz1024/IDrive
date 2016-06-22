package com.ccpress.izijia.entity;

import com.trs.types.UserInfoEntity;
import net.endlessstudio.util.json.JSONObjectHelper;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by WLH on 2015/5/18 11:55.
 * 网友上传的线路详情实体类(目的地、线路详情)
 */
public class LinesDetailUploadEntity implements Serializable {

    /**
     *看点实体类
     */
    public static class ViewSpot implements Serializable{
        private String id;
        private String spotid;
        private String name;
        private String geo;
        private String desc;
        private String type;
        private String imagae;
        private String isCart;
        private String isFavorite;

        public ViewSpot(){}
        public ViewSpot(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            setName(helper.getString("id",null));
            setType(helper.getString("type",null));
            setSpotid(helper.getString(new String[]{"spotid","soptid"},null));
            setName(helper.getString("name",null));
            setGeo(helper.getString("geo",null));
            setDesc(helper.getString("desc", null));
            setImagae(helper.getString("image",""));
            isCart = helper.getString("isCart","0");
            isFavorite = helper.getString("isFavorite","0");
        }
        public void setIsFavorite(String isFavorite){
            this.isFavorite = isFavorite;
        }

        public void setIsCart(String isCart) {
            this.isCart = isCart;
        }
        public void setSpotid(String spotid){
            this.spotid = spotid;
        }
        public void setId(String id){
            this.id = id;
        }
        public void setType(String type){
            this.type = type;
        }

        public void setName(String name){
            this.name = name;
        }
        public void setGeo(String geo){
            this.geo = geo;
        }
        public void setDesc(String desc){
            this.desc =desc;
        }
        public void setImagae(String imagae){
            this.imagae = imagae;
        }
        public String getSpotid(){
            return  spotid;
        }
        public String getName(){
            return name;
        }
        public String getGeo(){
            return  geo;
        }
        public String getDesc(){
            return desc;
        }
        public String getImagae(){
            return imagae;
        }
        public String getIsCart(){
            return  isCart;
        }
        public String getId(){
            return  id;
        }
        public String getType(){
            return  type;
        }


        public String getIsFavorite(){
            return isFavorite;
        }

    }

    /**
     *游记实体类
     */
    public class TravelNote implements Serializable{
        private ArrayList<NoteImage> images =  new ArrayList<NoteImage>();
        private String date;
        private String  title;

        public class NoteImage implements Serializable{
            private String image;
            private String  desc;

            public NoteImage(JSONObject object){
                JSONObjectHelper helper = new JSONObjectHelper(object);
                setImages(helper.getString("image", null));
                setDesc(helper.getString("desc", null));
            }

            public void setImages(String images){
                this.image = images;
            }
            public String getImage(){
                return image;
            }
            public void setDesc(String desc){
                this.desc = desc;
            }
            public String getDesc(){
                return desc;
            }
        }


        public TravelNote(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            setDate(helper.getString("date", null));
            setTitle(helper.getString("title", null));

            if(object.has("images")){
                try{
                    JSONArray mediasArray = object.getJSONArray("images");
                    for(int i = 0; i < mediasArray.length(); i ++){
                        NoteImage topic = new NoteImage(mediasArray.getJSONObject(i));
                        images.add(topic);
                    }
                }
                catch(JSONException e){
                    e.printStackTrace();
                }
            }
        }


        public void setDate(String date){
            this.date = date;
        }
        public void setTitle(String title){
            this.title = title;
        }
        public ArrayList<NoteImage> getImages(){
            return images;
        }
        public String getDate(){
            return date;
        }
        public String getTitle(){
            return title;
        }
    }

    /**
     *看点实体类
     */
    public class Trip implements Serializable{
        private int tripid;
        private String tripname;

        public Trip(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            setTripid(helper.getInt("tripid", -1));
            setTripname(helper.getString("tripname", null));
        }

        public void setTripid(int tripid){
            this.tripid = tripid;
        }
        public int getTripid(){
            return tripid;
        }
        public void setTripname(String tripname){
            this.tripname = tripname;
        }
        public String getTripname(){
            return tripname;
        }
    }
    /**
     *图文形式的实体类（目的地详情的美食、娱乐、购物、特产、文化； 线路详情的玩法、停车发呆地、美食）
     */
    public class ImageText implements Serializable{
        private String name;
        private String image;
        private String  desc;
        private String pid;
        private String isCart;
        private String isFavorite;


        public ImageText(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            setName(helper.getString("name", null));
            setImages(helper.getString("image", null));
            setDesc(helper.getString("desc", null));
            setPid(helper.getString("pid", null));
            isCart = helper.getString("isCart","0");
            isFavorite = helper.getString("isFavorite","0");
        }

        public void setImages(String images){
            this.image = images;
        }
        public String getImage(){
            return image;
        }
        public void setDesc(String desc){
            this.desc = desc;
        }
        public String getDesc(){
            return desc;
        }

        public void setName(String name){
            this.name = name;
        }

        public String getName(){
            return name;
        }

        public void setPid(String pid){
            this.pid = pid;
        }
        public String getPid(){
            return  pid;
        }

        public void setIsCart(String isCart){
            this.isCart = isCart;
        }
        public String getIsCart(){
            return isCart;
        }
        public void setIsFavorite(String isFavorite){
            this.isFavorite = isFavorite;
        }
        public String getIsFavorite(){
            return isFavorite;
        }
    }


    /**
     *旅馆，餐馆实体类
     */
    public class Hotel implements Serializable{
        private String name;
        private String addr;
        private String tel;
        private String price;
        private String star;
        private String image;
        private String  desc;

        public Hotel(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            setName(helper.getString("name", null));
            setAddr(helper.getString("addr", null));
            setTel(helper.getString("tel", null));
            setPrice(helper.getString("price", null));
            setStar(helper.getString("star", null));
            setImage(helper.getString("image", null));
            setDesc(helper.getString("desc", null));
        }

        public void setName(String name){
            this.name = name;
        }
        public String getName(){
            return name;
        }

        public void setAddr(String addr){
            this.addr = addr;
        }
        public String getAddr(){
            return addr;
        }
        public void setTel(String tel){
            this.tel = tel;
        }
        public String getTel(){
            return tel;
        }

        public void setPrice(String price){
            this.price = price;
        }
        public String getPrice(){
            return price;
        }

        public void setStar(String star){
            this.star = star;
        }
        public String getStar(){
            return star;
        }

        public void setImage(String image){
            this.image = image;
        }
        public String getImage(){
            return image;
        }

        public void setDesc(String desc){
            this.desc = desc;
        }
        public String getDesc(){
            return desc;
        }
    }

    /**
     *途经目的地实体类
     */
    public class Pathway implements Serializable{
        private String cname;
        private String cid;

        public Pathway(JSONObject object){
            JSONObjectHelper helper = new JSONObjectHelper(object);
            setCname(helper.getString("cname",null));
            setCid(helper.getString("cid",null));
        }
        public void setCname(String cname){
            this.cname = cname;
        }
        public String getCname(){
            return cname;
        }
        public void setCid(String cid){
            this.cid = cid;
        }
        public String getCid(){
            return cid;
        }


    }


/**********************************成员变量**************************************************/
    private SummaryEntity summary;//公有字段

    private ArrayList<Trip> trip = new ArrayList<Trip>();//目的地详情、线路详情字段

    private ArrayList<ImageText> food = new ArrayList<ImageText>();//目的地详情、线路-行程详情字段 美食
    private ArrayList<ImageText> special = new ArrayList<ImageText>();//目的地详情字段 特产
    private ArrayList<ImageText> yule = new ArrayList<ImageText>();//目的地详情字段 娱乐
    private ArrayList<ImageText> shopping = new ArrayList<ImageText>();//线路-行程详情字段、目的地详情字段 购物
    private ArrayList<ImageText> culture = new ArrayList<ImageText>();//目的地详情字段 文化
    private ArrayList<SummaryEntity> refway = new ArrayList<SummaryEntity>();//目的地详情、线路-行程详情 延伸线路

    private ArrayList<ViewSpot> viewspot = new ArrayList<ViewSpot>();////网友上传的线路详情、线路-行程看点字段
    private ArrayList<TravelNote> travel = new ArrayList<TravelNote>();//网友上传的线路详情才有的字段
    private UserInfoEntity user;//网友上传的线路详情才有的字段

    private ArrayList<ImageText> playrule = new ArrayList<ImageText>();//线路-行程详情字段  玩法
    private ArrayList<ImageText> parking = new ArrayList<ImageText>();//线路-行程详情字段  停车发呆地

    private ArrayList<Hotel> hotel = new ArrayList<Hotel>();//线路-行程详情字段  旅馆
    private ArrayList<Hotel> restaurant = new ArrayList<Hotel>();//线路-行程详情字段  餐厅

    private ArrayList<Pathway> pathway = new ArrayList<Pathway>();//线路-行程详情字段  途经目的地
    private ArrayList<CareChooseEntity.Extra> extras = new ArrayList<>();//线路-行程详情字段


    public LinesDetailUploadEntity(JSONObject object){
        JSONObjectHelper  helper = new JSONObjectHelper(object);

        if(object.has("summary")){
            try {
                JSONObject object1 = object.getJSONObject("summary");
                if(object1 != null){
                    summary = new SummaryEntity(object1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(object.has("user")){
            setUser(new UserInfoEntity(helper.getJSONObject("user",new JSONObject())));
        }

        if(object.has("trip")){
            try {
                JSONArray mediasArray = object.getJSONArray("trip");
                for(int i = 0; i < mediasArray.length(); i ++){
                    Trip topic = new Trip(mediasArray.getJSONObject(i));
                    trip.add(topic);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(object.has("viewspot")){
            try{
                JSONArray mediasArray = object.getJSONArray("viewspot");
                for(int i = 0; i < mediasArray.length(); i ++){
                    ViewSpot topic = new ViewSpot(mediasArray.getJSONObject(i));
                    viewspot.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        if(object.has("travel")){
            try{
                JSONArray mediasArray = object.getJSONArray("travel");
                for(int i = 0; i < mediasArray.length(); i ++){
                    TravelNote topic = new TravelNote(mediasArray.getJSONObject(i));
                    travel.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("food")){
            try{
                JSONArray mediasArray = object.getJSONArray("food");
                for(int i = 0; i < mediasArray.length(); i ++){
                    ImageText topic = new ImageText(mediasArray.getJSONObject(i));
                    food.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("special")){
            try{
                JSONArray mediasArray = object.getJSONArray("special");
                for(int i = 0; i < mediasArray.length(); i ++){
                    ImageText topic = new ImageText(mediasArray.getJSONObject(i));
                    special.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("yule")){
            try{
                JSONArray mediasArray = object.getJSONArray("yule");
                for(int i = 0; i < mediasArray.length(); i ++){
                    ImageText topic = new ImageText(mediasArray.getJSONObject(i));
                    yule.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("shopping")){
            try{
                JSONArray mediasArray = object.getJSONArray("shopping");
                for(int i = 0; i < mediasArray.length(); i ++){
                    ImageText topic = new ImageText(mediasArray.getJSONObject(i));
                    shopping.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("culture")){
            try{
                JSONArray mediasArray = object.getJSONArray("culture");
                for(int i = 0; i < mediasArray.length(); i ++){
                    ImageText topic = new ImageText(mediasArray.getJSONObject(i));
                    culture.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("refway")){
            try{
                JSONArray mediasArray = object.getJSONArray("refway");
                for(int i = 0; i < mediasArray.length(); i ++){
                    SummaryEntity topic = new SummaryEntity(mediasArray.getJSONObject(i));
                    refway.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("playrule")){
            try{
                JSONArray mediasArray = object.getJSONArray("playrule");
                for(int i = 0; i < mediasArray.length(); i ++){
                    ImageText topic = new ImageText(mediasArray.getJSONObject(i));
                    playrule.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("parking")){
            try{
                JSONArray mediasArray = object.getJSONArray("parking");
                for(int i = 0; i < mediasArray.length(); i ++){
                    ImageText topic = new ImageText(mediasArray.getJSONObject(i));
                    parking.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("hotel")){
            try{
                JSONArray mediasArray = object.getJSONArray("hotel");
                for(int i = 0; i < mediasArray.length(); i ++){
                    Hotel topic = new Hotel(mediasArray.getJSONObject(i));
                    hotel.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("restaurant")){
            try{
                JSONArray mediasArray = object.getJSONArray("restaurant");
                for(int i = 0; i < mediasArray.length(); i ++){
                    Hotel topic = new Hotel(mediasArray.getJSONObject(i));
                    restaurant.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

        if(object.has("pathway")){
            try{
                JSONArray mediasArray = object.getJSONArray("pathway");
                for(int i = 0; i < mediasArray.length(); i ++){
                    Pathway topic = new Pathway(mediasArray.getJSONObject(i));
                    pathway.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        if(object.has("extras")){
            try{
                JSONArray mediasArray = object.getJSONArray("extras");
                for(int i = 0; i < mediasArray.length(); i ++){
                    CareChooseEntity.Extra topic = new CareChooseEntity.Extra(mediasArray.getJSONObject(i));
                    extras.add(topic);
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }

    }
    public void setViewspot(ArrayList<ViewSpot> viewspot){
        if(viewspot == null){
            return;
        }
        if(this.viewspot == null){
            this.viewspot = new ArrayList<ViewSpot>();
        }
        this.viewspot.clear();
        this.viewspot.addAll(viewspot);
    }
    public void setTravel(){

    }

    public void setUser(UserInfoEntity entity){
        this.user = entity;
    }


    public ArrayList<ViewSpot> getViewspot(){
        return viewspot;
    }
    public ArrayList<TravelNote> getTravel(){
        return travel;
    }
    public UserInfoEntity getUser(){
        return user;
    }

    public SummaryEntity getSummary(){
        return summary;
    }

    public ArrayList<Trip>  getTrip(){
        return trip;
    }

    public ArrayList<ImageText> getFood(){
        return food;
    }

    public ArrayList<ImageText> getSpecial(){
        return special;
    }

    public ArrayList<ImageText> getShopping(){
        return shopping;
    }

    public ArrayList<ImageText> getCulture(){
        return culture;
    }

    public ArrayList<ImageText> getYule(){
        return yule;
    }

    public ArrayList<SummaryEntity> getRefway(){
        return refway;
    }

    public ArrayList<Pathway> getPathway(){
        return pathway;
    }
    public ArrayList<Hotel> getHotel(){
        return hotel;
    }
    public ArrayList<Hotel> getRestaurant(){
        return restaurant;
    }

    public ArrayList<ImageText> getPlayrule(){
        return playrule;
    }
    public ArrayList<ImageText> getParking(){
        return parking;
    }
    public ArrayList<CareChooseEntity.Extra> getExtras(){
        return extras;
    }


}
