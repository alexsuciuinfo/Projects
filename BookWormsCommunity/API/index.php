<?php

require_once './include/DataBaseController.php';
require '././libs/Slim/Slim.php';

\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();

$user_id = NULL;

/*
 * Register endpoint
 */
$app->post('/register', function() use ($app) {

    $response = array();

    $firstname = $app->request->post('firstname');
    $lastname = $app->request->post('lastname');
    $username = $app->request->post('username');
    $password = $app->request->post('password');
    $birthdate = $app->request->post('birthdate');
    $encoded_string = $app->request()->post('encoded_string');


    $database = new DataBaseController();
    $result = $database->registerUser($firstname, $lastname, $username, $password, $birthdate, $encoded_string);


    if ($result == USER_REGISTER_SUCCESS) {
    	$user_info = $database->getUserInfoByUsername($username);
    	$response["apikey"] = $user_info["apy_key"];
        $response["error"] = false;
        $response["message"] = "Success";
        $response["userId"] = $user_info["userId"];
    } else if ($result == USER_EXISTED) {
        $response["error"] = true;
        $response["message"] = "Username already exist";
    } else {
        $response["error"] = true;
        $response["message"] = "There was a problem with server";
    }

    generateJSONResponse($response);
});

/*
 * Verify if user is allowed to make specific changes
 * api_key
 */

function isAuthorized(\Slim\Route $route) {

    $head = apache_request_headers();
    $response = array();
    $app = \Slim\Slim::getInstance();

    if (isset($head['Authorization'])) {
        $database = new DataBaseController();

        $api_key = $head['Authorization'];
        global $user_id;
        $user = $database->getUserIdByApiKey($api_key);
        if ($user != NULL) {
            $user_id = $user["id"];
        }
    } else {
        $response["error"] = true;
        $response["message"] = "No Apikey";
        generateJSONResponse($response);
        $app->stop();
    }
}

/*
 * Login 
 * /login
 * method - Post
 * parameters - username, password
 */
$app->post('/login', function() use ($app) {

    $username = $app->request()->post('username');
    $password = $app->request()->post('password');
    $response = array();

    $database = new DataBaseController();

    if ($database->checkLogin($username, $password)) {
        $user_info = $database->getUserInfoByUsername($username);

        if ($user_info != NULL) {
            $response["error"] = false;
            $response["apikey"] = $user_info["apy_key"];
            $response["userId"] = $user_info["userId"];
        } else {
            $response["error"] = true;
            $response["message"] = "Server error";
        }
    } else {
        $response["error"] = true;
        $response["message"] = "Incorrect username or password";
    }
    generateJSONResponse($response);
});

/*
 * UpadateInfo
 * /updateInfo
 * parameters - firstname, lastname, birthdate, photo
 */
$app->post('/updateInfo','isAuthorized', function() use ($app) {

    $firstname = $app->request()->post('firstname');
    $lastname = $app->request()->post('lastname');
    $birthdate = $app->request()->post('birthdate');
    $photo = $app->request()->post('photo');
    $response = array();

    $database = new DataBaseController();

    global $user_id;

    echo ($user_id);
    if ($database->updateUserInfo($user_id, $firstname, $lastname, $birthdate, $photo)) {
        $response["error"] = false;
    } else {
        $response["error"] = true;
    }
    generateJSONResponse($response);
});

/*
 * AddReview
 * /createReview
 * parameters - json {book_id, content}
 */
$app->post('/addReview', 'isAuthorized', function() use($app) {

   $json_data = $app->request()->getBody();
   $json_decoded = json_decode($json_data);
   $book_id = $json_decoded->bookId;
   $review = $json_decoded->message;
   $date = $json_decoded->date;
   $database = new DataBaseController();
   
   global $user_id;
   
   $response = $database->addBookToRead($user_id, $book_id, $date);
   $response = $database->removeBookFromWanted($user_id, $book_id);
   $response = $database->addReview($user_id, $book_id, $review, $date);
   
   
   if ($response == true) 
   generateJSONResponse("OK");
   else generateJSONResponse("NOTOK");
    
});

/*
* EditReview
*/
$app->post('/editReview', 'isAuthorized', function() use($app) {

   $json_data = $app->request()->getBody();
   $json_decoded = json_decode($json_data);
   $book_id = $json_decoded->bookId;
   $review = $json_decoded->message;
   $date = $json_decoded->date;
   $response = true;
   $database = new DataBaseController();
   
   global $user_id;
  
   $response = $database->editReview($user_id, $book_id, $review, $date);
   if($response) 
   generateJSONResponse($book_id);
   else generateJSONResponse("NOTOK");
    
});


/*
* GetUserBooks
* /getUserBooks
*/

$app->post('/getUserBooks', 'isAuthorized', function() use($app) {

   $order = $app->request()->post('order');
   $type = $app->request()->post('type');
   $response = array();
   $database = new DataBaseController();
   
   $user_id = $app->request()->post('userId');
   
   $result = $database->getUserBooksById($user_id, $order, $type);
   
   while($book = $result->fetch_assoc()) {
	     $temp = array();
	     $temp["id"] = $book["id"];
	     $temp["title"] = $book["title"];
	     $temp["author"] = $book["author"];
	     $temp["rating"] = $book["rating"];
	     $temp["nrRatings"] = $book["nrRatings"];
	     $temp["imageURL"] = $book["imageURL"];
	     $temp["date"] = $book["date"];
	     $temp["category"] = $book["category"];
	     $temp["nrRead"] = $book["nrRead"];
	     array_push($response, $temp);
	}
   
   generateJSONResponse($response);
    
});

$app->post('/getUserBookss', function() use($app) {

   $order = $app->request()->post('order');
   $type = $app->request()->post('type');
   $response = array();
   $database = new DataBaseController();
   
   $user_id = $app->request()->post('userId');
   
   $result = $database->getUserBooksById($user_id, $order, $type);
   
   while($book = $result->fetch_assoc()) {
	     $temp = array();
	     $temp["id"] = $book["id"];
	     $temp["title"] = $book["title"];
	     $temp["author"] = $book["author"];
	     $temp["rating"] = $book["rating"];
	     $temp["nrRatings"] = $book["nrRatings"];
	     $temp["imageURL"] = $book["imageURL"];
	     $temp["date"] = $book["date"];
	     $temp["category"] = $book["category"];
	     array_push($response, $temp);
	}
   
   generateJSONResponse($response);
    
});


/*
* get User Read Books
*/
$app->post('/getUserReadBooks', 'isAuthorized', function() use($app) {

   $order = $app->request()->post('order');
   $type = $app->request()->post('type');
   $user_id = $app->request()->post('userId');
   $response = array();
   $database = new DataBaseController();
   

 
   $result = $database->getUserReadBooksById($user_id, $order, $type);
   
   while($book = $result->fetch_assoc()) {
	     $temp = array();
	     $temp["id"] = $book["id"];
	     $temp["title"] = $book["title"];
	     $temp["author"] = $book["author"];
	     $temp["rating"] = $book["rating"];
	     $temp["nrRatings"] = $book["nrRatings"];
	     $temp["nrRead"] = $book["nrRead"];
	     $temp["imageURL"] = $book["imageURL"];
	     $temp["date"] = $book["date"];
	     $temp["category"] = $book["category"];
	     array_push($response, $temp);
	}
   
   generateJSONResponse($response);
    
});

/*
* search books
*/
$app->post('/searchBooks', 'isAuthorized', function() use($app) {

   $string = $app->request()->post('search');
   $response = array();
   $database = new DataBaseController();
   
   
   $result = $database->searchBooks($string);
   
   while($book = $result->fetch_assoc()) {
	     $temp = array();
	     $temp["id"] = $book["id"];
	     $temp["title"] = $book["title"];
	     $temp["author"] = $book["author"];
	     $temp["rating"] = $book["rating"];
	     $temp["nrRatings"] = $book["nrRatings"];
	     $temp["nrRead"] = $book["nrRead"];
	     $temp["imageURL"] = $book["imageURL"];
	     $temp["category"] = $book["category"];
	     array_push($response, $temp);
	}
   
   generateJSONResponse($response);
    
});


/*
* get User Wanted Books
*/
$app->post('/getUserWantedBooks', 'isAuthorized', function() use($app) {

   $order = $app->request()->post('order');
   $type = $app->request()->post('type');
   $response = array();
   $database = new DataBaseController();
   
   $user_id = $app->request()->post('userId');
   
   $result = $database->getUserWantedBooksById($user_id, $order, $type);
   
   while($book = $result->fetch_assoc()) {
	     $temp = array();
	     $temp["id"] = $book["id"];
	     $temp["title"] = $book["title"];
	     $temp["author"] = $book["author"];
	     $temp["rating"] = $book["rating"];
	     $temp["nrRatings"] = $book["nrRatings"];
	     $temp["nrRead"] = $book["nrRead"];
	     $temp["imageURL"] = $book["imageURL"];
	     $temp["date"] = $book["date"];
	     $temp["category"] = $book["category"];
	     array_push($response, $temp);
	}
   
   generateJSONResponse($response);
    
});

/*
* geUserSuggestion
*/
$app->get('/getUserSuggestion', 'isAuthorized', function() use($app) {

   $response = array();
   $database = new DataBaseController();
   
   global $user_id;
   
   $result = $database->getUserReadBooksById($user_id, '1', "");
   
   
   while($book = $result->fetch_assoc()) {
   	     $ok = false;
	     $temp = array();
	     $temp["bookRead"]["id"] = $book["id"];
	     $temp["bookRead"]["title"] = $book["title"];
	     $result2 = $database->getUserRecom($book["author"], $book["category"], $user_id);
	     while ($book2 = $result2->fetch_assoc()) {
	        $ok = true;
	     	$temp["bookSuggestion"]["id"] = $book2["id"];
	     	$temp["bookSuggestion"]["title"] = $book2["title"];
	        $temp["bookSuggestion"]["author"] = $book2["author"];
	     	$temp["bookSuggestion"]["category"] = $book2["category"];
	     	$temp["bookSuggestion"]["imageURL"] = $book2["imageURL"];
	        $temp["message"] = "Because you read " . $temp["bookRead"]["title"] . " you might also like ";	
	     }
	     
	     if ($ok == true)
	     array_push($response, $temp);
	}
   
   generateJSONResponse($response);
    
});

/*
* getCategories
*/
$app->get('/getCategories', 'isAuthorized', function() use($app) {

   $response = array();
   $temp = array();
   $database = new DataBaseController();
   
   global $user_id;
   
   $result = $database->getCategoryNames();
   
   
   while($category = $result->fetch_assoc()) {
   	     array_push($temp,$category['category']);
  
	}
	$response['name'] = $temp;
   generateJSONResponse($temp);
    
});

/*
* getBooksByCategories
*/
$app->post('/getBooksByCategories', 'isAuthorized', function() use($app) {

   $categories = $app->request()->post("categories");
   $type = $app->request()->post("type");
   $order = $app->request()->post("order");
   $response = array();
   
   $categ_list; $categ;
   $database = new DataBaseController();
   
   global $user_id;
   
   $categ = explode(",", $categories);
   for ($i = 0; $i < sizeof($categ); $i++){
   	$categ_list[$categ[$i]] = array();
   }
   
   $result = $database->getUserReadBooksById($user_id, $order, $type);
   
   while($book = $result->fetch_assoc()) {
	     if (in_array($book["category"], $categ)) {
	     
	     $temp = array();
	     $temp["id"] = $book["id"];
	     $temp["title"] = $book["title"];
	     $temp["author"] = $book["author"];
	     $temp["rating"] = $book["rating"];
	     $temp["nrRatings"] = $book["nrRatings"];
	     $temp["nrRead"] = $book["nrRead"];
	     $temp["imageURL"] = $book["imageURL"];
	     $temp["date"] = $book["date"];
	     $temp["category"] = $book["category"];
	     array_push($categ_list[$book["category"]], $temp);
	     }
	     
	     for ($i = 0; $i < sizeof($categ); $i++){
	        $categ_array = array();
	        $categ_array['category'] = $categ[$i];
	        $categ_array['book'] = $categ_list[$categ[$i]];
   		array_push($response, $categ_array);
   	}

	}
   
   generateJSONResponse($response);
    
});

/*
* getTopRatedBooksThisWeek
*/
$app->get('/getNews', 'isAuthorized', function() use($app) {

   $response = array();
   $database = new DataBaseController();
   
   global $user_id;
   
   $result = $database->getTopRatedBooksThisWeek();
 
   
   while($book = $result->fetch_assoc()) {
	     $temp = array();
	     $temp["book"]["id"] = $book["id"];
	     $temp["book"]["title"] = $book["title"];
	     $temp["book"]["author"] = $book["author"];
	     $temp["book"]["imageURL"] = $book["imageURL"];
	     $temp["book"]["category"] = $book["category"];
	     $temp["book"]["rating"] = $book["rating"];
	     $temp["book"]["nrRatings"] = $book["nrRatings"] . " ratings";
	     $temp["message"] = "Top rated Book this Week in " . $book["category"] ;
	     $temp["thisWeek"] = "This week average rating : " . $book["thisWeek"];
	     array_push($response, $temp);
	}

    $result = $database->getMostReadBooksThisWeek();
            while($book = $result->fetch_assoc()) {
                $temp = array();
                $temp["book"]["id"] = $book["id"];
                $temp["book"]["title"] = $book["title"];
                $temp["book"]["author"] = $book["author"];
                $temp["book"]["imageURL"] = $book["imageURL"];
                $temp["book"]["category"] = $book["category"];
                $temp["book"]["rating"] = $book["rating"];
                $temp["book"]["nrRead"] = $book["nrRead"] . " readers";
                $temp["message"] = "Most read Book this Week in " . $book["category"] ;
                $temp["thisWeek"] = "This week readers : " . $book["thisWeek"];
                array_push($response, $temp);
            }
	
   
   generateJSONResponse($response);
    
});





/*
 * getBooks
*/
$app->get('/getBooks', function() use ($app) {
	
	$response = array();
	$database = new DataBaseController();
	
	$result = $database->getBooks();
	
	while($book = $result->fetch_assoc()) {
	     $temp = array();
	     $temp["id"] = $book["id"];
	     $temp["title"] = $book["title"];
	     $temp["author"] = $book["author"];
	     $temp["rating"] = $book["rating"];
	     $temp["nrRatings"] = $book["nrRatings"];
	     $temp["imageURL"] = $book["imageURL"];
	     array_push($response, $temp);
	}
	
	generateJSONResponse($response);
}); 

/*
*	getBookInfoById
*/
$app->post('/getBookInfoById', 'isAuthorized', function() use($app) {

   $book_id = $app->request()->post('bookId');
   $response = array();
   $database = new DataBaseController();
   
   global $user_id;
   
   $result = $database->getBookInfoById($user_id, $book_id);
   
   while($book = $result->fetch_assoc()) {
	     $temp = array();
	     $temp["book"]["id"] = $book["id"];
	     $temp["book"]["title"] = $book["title"];
	     $temp["book"]["author"] = $book["author"];
	     $temp["book"]["rating"] = $book["rating"];
	     $temp["book"]["nrRatings"] = $book["nrRatings"];
	     $temp["book"]["imageURL"] = $book["imageURL"];
	     $temp["book"]["nrRead"] = $book["nrRead"];
	     $temp["book"]["description"] = $book["description"];
	     $temp["wantBook"] = $book["wantBook"];
	     $temp["readBook"] = $book["readBook"];
	     $temp["myRating"] = $book["myRating"];
	     $temp["description"] = $book["description"];
	     $temp["book"]["category"] = $book["category"];

	}
	
	
   
   generateJSONResponse($temp);
    
});

/*
* getUserInfo
*/
$app->get('/getUserInfo', 'isAuthorized', function() use($app) {

   $response = array();
   $database = new DataBaseController();
   
   global $user_id;
   
   $result = $database->getUserInfo($user_id);
   
   while($book = $result->fetch_assoc()) {
	     $temp = array();
	     $temp["id"] = $book["id"];
	     $temp["username"] = $book["username"];
	     $temp["firstname"] = $book["firstname"];
	     $temp["lastname"] = $book["lastname"];
	     $temp["location"] = $book["location"];
	     $temp["photo"] = $book["photo"];
	     $temp["gender"] = $book["gender"];
	     $temp["birthdate"] = $book["birthdate"];
	}

   
   generateJSONResponse($temp);
    
});

$app->post('/getUserReview', 'isAuthorized', function() use($app) {
	
   $book_id = $app->request()->post("bookId");	
   $response = array();
   $database = new DataBaseController();
   
   global $user_id;
   $temp = array();
   $ok = true;
   
   $result = $database->getUserReview($book_id, $user_id);
   
   while($book = $result->fetch_assoc()) {
	     $temp["message"] = $book["review"];
	     $temp["date"] = $book["date"];
	     $ok = false;
	}

   if ($ok == true)
   {
   	 $temp["message"] = null;
   	 $temp["date"] = null;
   }
   
   generateJSONResponse($temp);
    
});


/*
*  get BookReviews
*/

$app->post('/getBookReviews', 'isAuthorized', function() use($app) {

   $book_id = $app->request()->post('bookId');
   $response = array();
   $database = new DataBaseController();
   
   global $user_id;
   
   $result = $database->getBookReviews($book_id, $user_id);
   
   while($book = $result->fetch_assoc()) {
	     $temp = array();
	     $temp["user"]["userId"] = $book["id"];
	     $temp["user"]["username"] = $book["username"];
	     $temp["user"]["photo"] = $book["photo"];
	     $temp["review"]["message"] = $book["review"];
	     $temp["review"]["date"] = $book["date"];
	     $temp["review"]["rating"] = null;
	     $result2 = $database->getUserRating($book_id, $temp["user"]["userId"]);
	     while ($rating = $result2->fetch_assoc()) {
	     	$temp["review"]["rating"] = $rating["rating"];
	     }
	     array_push($response, $temp);
	}
	
   $result = $database->getBookRatings($book_id, $user_id);
   
   while($book = $result->fetch_assoc()) {
	     $result2 = $database->getUserReview($book_id, $book["id"]);
	     if (! $result2->fetch_assoc()){
	     $temp = array();
	     $temp["user"]["userId"] = $book["id"];
	     $temp["user"]["username"] = $book["username"];
	     $temp["user"]["photo"] = $book["photo"];
	     $temp["review"]["message"] = null;
	     $temp["review"]["date"] = null;
	     $temp["review"]["rating"] = $book["rating"];
	     array_push($response, $temp);
	     }  
	}	
   
   generateJSONResponse($response);
    
});


/*
* addRating
*/
$app->post('/addRating', 'isAuthorized', function() use($app) {

   $book_id = $app->request()->post("bookId");
   $rating = $app->request()->post("rating");
   $date = $app->request()->post("date");
   $database = new DataBaseController();
   
   global $user_id;
   
   $response = $database->removeBookFromWanted($user_id, $book_id);
   $response = $database->addBookToRead($user_id, $book_id, $date);
   $response = $database->addRating($user_id, $book_id, $rating, $date);
   $response = $database->editRating($user_id, $book_id, $rating, $date);
   if ($response == true) 
   generateJSONResponse("OK");
   else generateJSONResponse("NOTOK");
    
});

/*
* delete Rating
*/
$app->post('/removeRating', 'isAuthorized', function() use($app) {

   $book_id = $app->request()->post("bookId");
   $database = new DataBaseController();
   
   global $user_id;
   
   $response = $database->removeRating($user_id, $book_id);

   if ($response == true) 
   generateJSONResponse("OK");
   else generateJSONResponse("NOTOK");
    
});

/*
* addBookToRead
*/
$app->post('/addBookToRead', 'isAuthorized', function() use($app) {

   $book_id = $app->request()->post("bookId");
   $date = $app->request()->post("date");
   $database = new DataBaseController();
   
   global $user_id;
   
   $response = $database->addBookToRead($user_id, $book_id, $date);
   $response = $database->removeBookFromWanted($user_id, $book_id);

   if ($response == true) 
   generateJSONResponse("OK");
   else generateJSONResponse("NOTOK");
    
});

/*
* addBookToWanted
*/
$app->post('/addBookToWanted', 'isAuthorized', function() use($app) {

   $book_id = $app->request()->post("bookId");
   $date = $app->request()->post("date");
   $database = new DataBaseController();
   
   global $user_id;
   
   $response = $database->addBookToWanted($user_id, $book_id, $date);
   $response = $database->removeBookFromRead($user_id, $book_id);
   $response = $database->removeReview($user_id, $book_id);
   $response = $database->removeRating($user_id, $book_id);

   if ($response == true) 
   generateJSONResponse("OK");
   else generateJSONResponse("NOTOK");
    
});
/*
* removeBookFromUserBooks
*/
$app->post('/removeBookFromUserBooks', 'isAuthorized', function() use($app) {

   $book_id = $app->request()->post("bookId");
   $database = new DataBaseController();
   
   global $user_id;
   
   $response = $database->removeBookFromWanted($user_id, $book_id);
   $response = $database->removeBookFromRead($user_id, $book_id);
   $response = $database->removeReview($user_id, $book_id);
   $response = $database->removeRating($user_id, $book_id);

   if ($response == true) 
   generateJSONResponse("OK");
   else generateJSONResponse("NOTOK");
    
});

/*
* remove review
*/
$app->post('/removeReview', 'isAuthorized', function() use($app) {

   $book_id = $app->request()->post("bookId");
   $database = new DataBaseController();
   
   global $user_id;
   
   $response = $database->removeReview($user_id, $book_id);

   if ($response == true) 
   generateJSONResponse("OK");
   else generateJSONResponse("NOTOK");
    
});

/*
* getUserInfo
*/
$app->post('/getUserInfo', 'isAuthorized', function() use($app) {

   $user_id = $app->request()->post("userId");
   $database = new DataBaseController();
  
   
   $result = $database->getUserInformation($user_id);
   
   $response = array();
   while($user = $result->fetch_assoc()) {
   	$response["firstname"] = $user["firstname"];
   	$response["lastname"] = $user["lastname"];
   	$response["birthdate"] = $user["birthdate"];
   	$response["username"] = $user["username"];
   	$response["gender"] = $user["gender"];
   	$response["location"] = $user["location"];
   	$response["about"] = $user["about"];
   	$response["nrReadBooks"] = $user["nrReadBooks"];
   	$response["nrWantedBooks"] = $user["nrWantedBooks"];
   	$response["nrRatings"] = $user["nrRatings"];
   	$response["nrReviews"] = $user["nrReviews"];
   	$response["photo"] = $user["photo"];
   };

   generateJSONResponse($response);

    
});

$app->post('/editUserInfo', 'isAuthorized', function() use($app) {
   
    $json_data = $app->request()->getBody();
    $json_decoded = json_decode($json_data);
    $username = $json_decoded->username;
    $firstname = $json_decoded->firstname;
    $lastname = $json_decoded->lastname;
    $birthdate = $json_decoded->birthdate;
    $location = $json_decoded->location;
    $gender = $json_decoded->gender;
    $about = $json_decoded->about;
    $enc_photo = $json_decoded->encryptedPhoto;
    
    $database = new DataBaseController();
    
    global $user_id;
   
    
    $result = $database->editUserInfo($user_id, $username, $firstname, $lastname, $birthdate, $location, $gender, $about);
    

    if ($result == USER_EDIT_SUCCESS) {
    	$response["error"] = false;
        $response["message"] = "Success";
        if ($enc_photo == "NOPHOTO") {
        	$database->removePhoto($user_id);
        } else if ($enc_photo == "NOCHANGE") {
        	
        } else {
        	$database->addPhoto($user_id, $enc_photo);
        }
    } else if ($result == USER_EXISTED) {
        $response["error"] = true;
        $response["message"] = "Username already exist";
    } else {
        $response["error"] = true;
        $response["message"] = "There was a problem with server";
    }

    	
    	
    generateJSONResponse($response);
    
});



/*
 * generate Response in Json format
 */
function generateJSONResponse($response) {
    $app = \Slim\Slim::getInstance();
    $app->contentType('application/json');
    echo json_encode($response);
}

$app->run();
?>