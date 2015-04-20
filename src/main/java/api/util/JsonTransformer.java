package api.util;

import com.google.gson.Gson;
import spark.ResponseTransformer;

/**
 * Created by Lorenzo on 15/04/2015.
 */
public class JsonTransformer implements ResponseTransformer {

	Gson gson = new Gson();

	@Override
	public String render(Object o) throws Exception {
		return gson.toJson(o);
	}
}
