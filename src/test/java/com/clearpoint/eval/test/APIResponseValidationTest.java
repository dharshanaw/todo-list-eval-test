package com.clearpoint.eval.test;

import com.clearpoint.eval.apiclient.ClearPointAPIClient;
import com.clearpoint.eval.model.CreateTodoItemRequestDto;
import com.clearpoint.eval.model.UpdateTodoItemRequestDto;
import com.clearpoint.eval.test.constants.ValidateConstants;
import com.clearpoint.eval.utils.TestUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.UUID;

/**
 * This test class will validate app the api methods of the to do items api
 */
public class APIResponseValidationTest {
    private ClearPointAPIClient api;
    String apiResourceUrl;
    String itemDescription = null;
    String itemId = null;

    @BeforeTest
    public void init() {
        apiResourceUrl = System.getProperty(ValidateConstants.API_BACKEND_URL_PROPERTY_NAME);
        api = new ClearPointAPIClient(apiResourceUrl);
        itemDescription = "Item-" + TestUtils.getRandomString(5);
    }

    @Test(description = "Create item in to do item list test")
    public void postItemToToDoList() {
        CreateTodoItemRequestDto body = new CreateTodoItemRequestDto();
        body.description(itemDescription);
        ResponseEntity response = api.apiTodoItemsPost(body);
        itemId = (String) response.getBody();
        int responseCode = response.getStatusCodeValue();
        Assert.assertEquals(responseCode ,HttpStatus.CREATED.value(),
                "Incorrect status code for Post response expected " + HttpStatus.CREATED + " available " + responseCode);
        Assert.assertTrue(verifyItemCreated(itemId, itemDescription), "Item with ID " + itemId + "Not created");
    }

    @Test(description = "Create item in to do item list test", dependsOnMethods = {"postItemToToDoList"})
    public void getItemByIdDoList() {
        ResponseEntity response = api.apiTodoItemsIdGet(UUID.fromString(itemId));
        int responseCode = response.getStatusCodeValue();
        Assert.assertEquals(responseCode , HttpStatus.OK.value(),
                "Incorrect status code for get response expected " + HttpStatus.OK + " available " + responseCode);
        JSONObject jo = new JSONObject(response.getBody().toString());
        Assert.assertTrue(jo.get("id").equals(itemId), "Incorrect item id posted expected "
                + itemId + "actual " + jo.get("id"));
        Assert.assertTrue(jo.get("description").equals(itemDescription), "Incorrect item description posted expected "
                + itemDescription + "actual " + jo.get("description"));
    }

    @Test(description = "Put item in to do item list test", dependsOnMethods = {"postItemToToDoList"})
    public void putItemToToDoList() {
        UpdateTodoItemRequestDto body = new UpdateTodoItemRequestDto();
        body.setId(UUID.fromString(itemId));
        itemDescription = "PutItem-" + TestUtils.getRandomString(5);
        body.description(itemDescription);
        body.setIsCompleted(false);
        ResponseEntity response = api.apiTodoItemsIdPut(UUID.fromString(itemId), body);
        int responseCode = response.getStatusCodeValue();
        Assert.assertTrue(responseCode == HttpStatus.NO_CONTENT.value(),
                "Incorrect status code for put response expected " + HttpStatus.NO_CONTENT + " available " + responseCode);
        Assert.assertTrue(verifyItemCreated(itemId, itemDescription), "Item with ID " + itemId + "Not created");
    }

    @Test(description = "Get all items in the list test",
            dependsOnMethods = {"postItemToToDoList", "putItemToToDoList"})
    public void getAllPostedItems() {
        ResponseEntity response = api.apiTodoItemsGet();
        int responseCode = response.getStatusCodeValue();
        Assert.assertTrue(responseCode == HttpStatus.OK.value(),
                "Incorrect status code for get response expected " + HttpStatus.OK + " available " + responseCode);
        JSONArray itemArray = new JSONArray(response.getBody().toString().replace("=", ":"));
        Assert.assertFalse(itemArray.isEmpty() , "Item get request returned as empty");
        boolean itemContains =itemArray.toList().stream().anyMatch(item ->
         ((HashMap)item).get("id").equals(itemId) && ((HashMap)item).get("description").equals(itemDescription));
        Assert.assertTrue(itemContains, "Added items not listed in the to do item list");
    }

    private boolean verifyItemCreated(String itemId, String description) {
        ResponseEntity response = api.apiTodoItemsIdGet(UUID.fromString(itemId));
        JSONObject jo = new JSONObject(response.getBody().toString());
        return !jo.isEmpty() ? jo.get("id").toString().equals(itemId) : false
                && jo.get("description").toString().equals(description);
    }
}