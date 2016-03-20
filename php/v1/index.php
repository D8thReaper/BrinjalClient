<?php
require '.././libs/Slim/Slim.php';
require_once 'dbHelper.php';

\Slim\Slim::registerAutoloader();
$app = new \Slim\Slim();
$app = \Slim\Slim::getInstance();
$db = new dbHelper();

/**
 * Database Helper Function templates
 */
/*
select(table name, where clause as associative array)
insert(table name, data as associative array, mandatory column names as array)
update(table name, column names as associative array, where clause as associative array, required columns as array)
delete(table name, where clause as array)
*/



// categories
$app->get('/categories', function() {
    global $db;
    $rows = $db->select("categories","id,name",array());
    echoResponse(200, $rows);
});

// sub_categories
$app->get('/sub_categories', function() {
    global $db;
    $rows = $db->select("sub_categories","id,name,category_id",array());
    echoResponse(200, $rows);
});

// sub_categories
$app->get('/sub_categories_test', function() {
    global $db;
    $rows = $db->selectjoin("sub_categories","categories","category_id","id",array());
    echoResponse(200, $rows);
});

// sub_categories
$app->get('/sub_categories/:category_id/:long/:lat', function($category_id) {
    global $db;
    $rows = $db->select("sub_categories","id,name,category_id",array('category_id'=>$category_id));
    echoResponse(200, $rows);
});

// sub_sub_categories
$app->get('/sub_sub_categories', function() {
    global $db;
    $rows = $db->select("sub_sub_categories","id,name,sub_category_id",array());
    echoResponse(200, $rows);
});

// sub_sub_category
$app->get('/sub_sub_category/:sub_category_id', function($sub_category_id) {
    global $db;
    $rows = $db->select("sub_sub_category","id,name,sub_category_id",array('sub_category_id'=>$sub_category_id));
    echoResponse(200, $rows);
});

// places
$app->get('/places/:subcat', function($subcat) {
    global $db;
    $rows = $db->select("places","id,title,longitude,latitude,thumb,deals,tags",array('subcat'=>$subcat));
    echoResponse(200, $rows);
});

// place_details
$app->get('/place_details/:id', function($id) {
    global $db;
    $rows = $db->select("places","id,title,subcat,subtitle,longitude,latitude,charge,locality,thumb,description,chain,rating,tags,phone,email,deals",array('id'=>$id));
    echoResponse(200, $rows);
});

// place_details
$app->get('/reviews/:id', function($id) {
    global $db;
    $rows = $db->select("xHackReview","name",array('id'=>$id));
    echoResponse(200, $rows);
});

// timings
$app->get('/timings/:placeid', function($placeid) {
    global $db;
    $rows = $db->select("timings","id,placeid,day,start,end,break_start,break-end",array('placeid'=>$placeid));
    echoResponse(200, $rows);
});
// images
$app->get('/images/:placeid', function($placeid) {
    global $db;
    $rows = $db->select("images","id,placeid,image",array('placeid'=>$placeid));
    echoResponse(200, $rows);
});

// tags
$app->get('/tags', function() {
    global $db;
    $rows = $db->select("tags","id,title,value",array());
    echoResponse(200, $rows);
});

// coupons
$app->get('/coupons/:placeid', function($placeid) {
    global $db;
    $rows = $db->select("coupons","id,title,placeid,type,cost,description,conditions",array('placeid'=>$placeid));
    echoResponse(200, $rows);
});

// deals
$app->get('/deals/:placeid', function($placeid) {
    global $db;
    $rows = $db->select("deals","id,placeid,title,type,description,timer,tags",array('placeid'=>$placeid));
    echoResponse(200, $rows);
});


// store_items
$app->get('/tags', function() {
    global $db;
    $rows = $db->selectjoin("store_items","users","userid","id",array());
    echoResponse(200, $rows);
});

// add store item 
$app->post('/user_details', function() use ($app) {
    $data = json_decode($app->request->getBody());
    $mandatory = array('name');
    global $db;
    $rows = $db->insert("store_items", $data, $mandatory);
    if($rows["status"]=="success")
        $rows["message"] = "Store item added succesfully.";
     else
     	$rows["message"] = "Problem in saving item";
    echoResponse(200, $rows);
});

// bookmarks
$app->get('/bookmarks/:userid', function($userid) {
    global $db;
    $rows = $db->select("bookmarks","id,placeid",array('userid'=>$userid));
    echoResponse(200, $rows);
});

// add bookmarks 
$app->post('/add_bookmarks', function() use ($app) {
    $data = json_decode($app->request->getBody());
    $mandatory = array('placeid','userid');
    global $db;
    $rows = $db->insert("bookmarks", $data, $mandatory);
    if($rows["status"]=="success")
        $rows["message"] = "Bookmark added succesfully.";
     else
        $rows["message"] = "Problem in saving bookmark";
    echoResponse(200, $rows);
});

$app->post('/user/:id', function($id) use ($app) {
    $data = json_decode($app->request->getBody());
    $condition = array('id'=>$id);
    $mandatory = array();
    global $db;
    $rows = $db->update("users", $data, $condition, $mandatory);
    if($rows["status"]=="success")
        $rows["message"] = "User information updated successfully.";
    echoResponse(200, $rows);
});


function echoResponse($status_code, $response) {
    global $app;
    $app->status($status_code);
    $app->contentType('application/json');
    echo json_encode($response,JSON_NUMERIC_CHECK);
}

$app->run();
?>