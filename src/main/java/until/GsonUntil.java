package until;

import com.google.gson.*;
import com.money.model.ActivityDetailModel;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by liumin on 15/7/5.
 */

@Component
public class GsonUntil {

    public static Gson getGson() {
        return gson;
    }

    private static Gson gson = null;

    GsonUntil(){
        gson = new GsonBuilder().registerTypeAdapter(ActivityDetailModel.class, new JsonSerializer<ActivityDetailModel>() {
            public JsonElement serialize(ActivityDetailModel src, Type typeOfSrc,
                                         JsonSerializationContext context) {
                JsonObject o=new JsonObject();
                o.addProperty("activityStageId", src.getActivityStageId());
                return o;
            }
        }).create();
    }

    /**
     * 将json转成成javaBean对象
     *
     * @param <T>
     * 返回类型
     * @param json
     * 字符串
     * @param clazz
     * 需要转换成的类
     * @return
     */
    public static <T> List<T> jsonListToJavaClass(String json, Type type) {
        List<T> list = new ArrayList<T>();
        try {
            list = gson.fromJson(json, type);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 将json转成成javaBean对象
     *
     * @param <T>
     * 返回类型
     * @param json
     * 字符串
     * @param clazz
     * 需要转换成的类
     * @return
     */
    public static <T> T jsonToJavaClass(String json, Type type) {
        T JavaCalss = null;
        try {
            JavaCalss = gson.fromJson(json, type);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return JavaCalss;
    }


    public static <T> String JavaClassToJson( T c ){
        String json = gson.toJson( c );
        return json;
    }


    public static <T> String JavaClassListToJsonList( List<T> list ){
        String json = gson.toJson( list );
        return json;
    }
}
