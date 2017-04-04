<?php

include_once 'Db.php';
define("USER_REGISTER_SUCCESS", 0);
define("USER_EXISTED", 1);
define("USER_REGISTER_FAIL", 2);
define("USER_EDIT_SUCCESS",3);

class DataBaseController {

    private $connection;
    private $pictureProfilePath = "ProfilePictures/";
    private  $url = "bookwormscomunity.com/";

    public function __construct() {
        $database = new DataBaseConnect();
        $this->connection = $database->connect();
    }

    //check if password is correct for this username
    public function checkLogin($username, $password) {
        require_once 'EncryptPassword.php';

        $query = "SELECT password from Users where username = ?";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("s", $username);
        $statement->execute();
        $statement->bind_result($enc_password);
        $statement->store_result();

        if ($statement->num_rows > 0) {
            $statement->fetch();
            $statement->close();

            if (EncryptPassword::check_password($enc_password, $password)) {
                return true;
            } else {
                return false;
            }
        } else {
            $statement->close();
            return false;
        }
    }

    //verify if user already existed
    private function userExists($username) {
        $query = $this->connection->prepare("SELECT username from Users where username = ?");
        $query->bind_param("s", $username);
        $query->execute();
        $query->store_result();
        $row_result = $query->num_rows;
        $query->close();

        if ($row_result > 0)
            return true;

        return false;
    }

    //insert userData into DataBase
    public function registerUser($firstname, $lastname, $username, $password, $birthdate, $encoded_string) {

        require_once 'EncryptPassword.php';
        if ($this->userExists($username)) {
            return USER_EXISTED;
        } else {
            $enc_password = EncryptPassword::encrypt_password($password);
            $api_key = $this->generateApiKey();
            $user_id = $this->generateUniqueId();
            $url = "http://bookwormscomunity.com/";
            $path;
            if ($encoded_string != null) {
            $decoded_string = base64_decode($encoded_string);
            $image_finalName = $user_id . "Photo.jpg";
            global $pictureProfilePath;
            $pictureProfilePath = "ProfilePictures/";
            $path = $pictureProfilePath . $image_finalName;
            $file = fopen($path, "wb");

            
            $is_written = fwrite($file, $decoded_string);
            fclose($file);
            $path = $url . $path;
	}
            $query = $this->connection->prepare("INSERT INTO Users(Id,firstname,lastname,username,password,birthdate,photo,apy_key) values(?,?,?,?,?,?,?,?)");

	    
            $query->bind_param("ssssssss", $user_id, $firstname, $lastname, $username, $enc_password, $birthdate, $path, $api_key);
            $result = $query->execute();
            echo($query->error);
            $query->close();

            if ($result) {
                return USER_REGISTER_SUCCESS;
            } else {
                return USER_REGISTER_FAIL;
            }
        }
    }
    
    public function removePhoto($user_id) {
    	    
    	    global $url;
            
            $image_finalName = $user_id . "Photo.jpg";
            global $pictureProfilePath;
            $pictureProfilePath = "ProfilePictures/";
            $path = $pictureProfilePath . $image_finalName;
            
            if(file_exists($path))
            unlink($path);

            $query = $this->connection->prepare("UPDATE Users set photo = ? where Id = ?");
	    
	    $path = null;
            $query->bind_param("ss", $path, $user_id);
            $result = $query->execute();
            echo($query->error);
            $query->close();
    }
    
    public function addPhoto($user_id, $encoded_string) {
    	    
    	    $url = "http://bookwormscomunity.com/";
    
    	    $decoded_string = base64_decode($encoded_string);
            $image_finalName = $user_id . "Photo.jpg";
            global $pictureProfilePath;
            $pictureProfilePath = "ProfilePictures/";
            $path = $pictureProfilePath . $image_finalName;
            $file = fopen($path, "wb");

            
            $is_written = fwrite($file, $decoded_string);
            fclose($file);
            
            $query = $this->connection->prepare("UPDATE Users set photo = ? where Id = ?");

	    $path = $url . $path;
            $query->bind_param("ss", $path, $user_id);
            $result = $query->execute();
            echo($query->error);
            $query->close();
    }

    //get UserInfo by username
    public function getUserInfoByUsername($username) {
        $query = "Select Id as userId, username, firstname, lastname, birthdate, photo, apy_key from Users where username = ?";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("s", $username);

        if ($statement->execute()) {
            $user_info = $statement->get_result()->fetch_assoc();
            $statement->close();
            return $user_info;
        } else {
            return NULL;
        }
    }

    //get UserId by ApiKey
    public function getUserIdByApiKey($apikey) {
        $query = "SELECT id from Users where apy_key = ?";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("s", $apikey);
        if ($statement->execute()) {
            $user_id = $statement->get_result()->fetch_assoc();
            $statement->close();
            return $user_id;
        } else {
            return NULL;
        }
    }

    //update UserInfo
    public function updateUserInfo($user_id, $firstname, $lastname, $birthdate, $photo) {
        $query = "Update Users set firstname = ? , lastname = ?, birthdate = ?, photo = ? where Id = ?";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("sssss", $firstname, $lastname, $birthdate, $photo, $user_id);

        if ($statement->execute()) {
            $statement->close();
            return true;
        } else {
            return false;
        }
    }

    //addReview to a Book
    public function addReview($user_id, $book_id, $review, $date) {
      
        $query = "INSERT INTO BooksReviews(bookid, userid, content, dateAdded) values(?, ?, ?, ?)";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("isss", $book_id, $user_id, $review, $date);

        if ($statement->execute()) {
            $statement->close();
            return true;
        } else {
            return false;
        }
    }
    
    //edit Review
     public function editReview($user_id, $book_id, $review, $date) {
      
        $query = "Update BooksReviews set content = ?, dateAdded = ? where bookId = ? and userId = ? ";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("ssis", $review, $date, $book_id, $user_id);
	if($statement->execute())
	return true;
    }
    

    //add Raiting to a Book
    public function addRating($user_id, $book_id, $rating, $date) {

        $query = "INSERT INTO BooksRatings(bookid, userid, star, dateAdded) values(?, ?, ?, ?)";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("isds", $book_id, $user_id, $rating, $date);
	
        if ($statement->execute()) {
            return true;
        } else {
            return false;
        }
    }
    
    //edit Rating
    public function editRating($user_id, $book_id, $rating, $date) {

        $query = "UPDATE BooksRatings set star = ?, dateAdded = ? where bookid = ? and userid = ?";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("dsis", $rating, $date, $book_id, $user_id);

        if ($statement->execute()) {
            return true;
        } else {
            return false;
        }
    }
    
    //remove Rating
    public function removeRating($user_id, $book_id) {
    	$query = "DELETE from BooksRatings where bookid = ? and userid = ?";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("is", $book_id, $user_id);

        if ($statement->execute()) {
            return true;
        } else {
            return false;
        }
    }
    
    //add book to UserReadBooks
    public function addBookToRead($user_id, $book_id, $date) {
    	$query = "INSERT INTO UsersReadBooks(bookid, userid, dateRead) values(?, ?, ?)";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("iss", $book_id, $user_id, $date);
	
        if ($statement->execute()) {
        return true;
        } else {
            return false;
        }
    }
    
    //add book to UsersWantBooks
    public function addBookToWanted($user_id, $book_id, $date) {

    	$query = "INSERT INTO UsersWantBooks(bookid, userid, dateAdded) values(?, ?, ?)";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("iss", $book_id, $user_id, $date);
	
        if ($statement->execute()) {
		return true;
        } else {
            return false;
        }
    }
    
    //remove book from UserReadBook
    public function removeBookFromRead($user_id, $book_id) {
    	$query = "DELETE FROM UsersReadBooks where bookid = ? and userid = ?";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("is", $book_id, $user_id);
        if ($statement->execute()) {
            return true;
        } else {
            return false;
        }
    }
    
    //remove book from UserWantBook
    public function removeBookFromWanted($user_id, $book_id) {
    	$query = "DELETE FROM UsersWantBooks where bookid = ? and userid = ?";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("is", $book_id, $user_id);
	
        if ($statement->execute()) {
            return true;
        } else {
            return false;
        }
    }
    
    //remove Review
    public function removeReview($user_id, $book_id) {
    	$query = "DELETE FROM BooksReviews where bookid = ? and userid = ?";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("is", $book_id, $user_id);
        if ($statement->execute()) {
            return true;
        } else {
            return false;
        }
    }

    //update Book raiting if a user add a new Raiting or Remove 
    public function updateBookRating($book_id, $star) {
        $query = "SELECT rating, nrRatings from Books where id = ?";
        $statement = $this->connection->prepare($query);
        $statement->bind_param("i", $book_id);
        if ($statement->execute()) {
            $result = $statement->get_result()->fetch_assoc();
            $curr_star = $result['rating'];
            $nr_rating = $result['nrRatings'];
            $nr_rating++;
            $statement->close();

            $query = "UPDATE Books set rating = ?, nrRatings = ? where id = ?";
            $statement = $this->connection->prepare($query);

            $new_rating = ($curr_star + $star) / $nr_rating;

            $statement->bind_param("dii", $new_rating, $nr_rating, $book_id);
            if ($statement->execute()) {
                $statement->close();
                return true;
            } else {
                echo($statement->error);
                return false;
            }
        }
    }
    
    //get books 
    public function getBooks() {
    	$query = "SELECT id, title, author, rating, nrRatings, imageURL from Books";
    	$statement = $this->connection->prepare($query);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //get CAtegory names
    public function getCategoryNames() {
    	$query = "SELECT name as category from BookTypes";
    	$statement = $this->connection->prepare($query);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    	
    }
    
    //get user books by Id
    public function getUserBooksById($user_id, $order, $type) {
    	$query = "SELECT * from (
    select b.id, title, a.name as author, imageURL, urb.dateRead as date ,  c.name as category,
     (select count(*) as nrRatings from BooksRatings
     where bookId = b.Id) as nrRatings,
     (select avg(star) from BooksRatings 
     where BookId = b.id) as rating,
     (select count(*) as nrRead from UsersReadBooks
     where bookId = b.Id) as nrRead
    from Books b join UsersReadBooks urb on
	b.Id = urb.BookId
     join Authors a on b.AuthorId = a.Id
 
     join BookTypes c on b.BookTypeId = c.Id
     where urb.UserId = ?
   	 group by b.Id
	UNION all
	select b.id, title, a.name as author, imageURL, urb.dateAdded as date ,  c.name as category,
     (select count(*) as nrRatings from BooksRatings
     where bookId = b.Id) as nrRatings,
     (select avg(star) from BooksRatings 
     where BookId = b.id) as rating,
         (select count(*) as nrRead from UsersReadBooks
     where bookId = b.Id) as nrRead
    from Books b join UsersWantBooks urb on
	b.Id = urb.BookId
     join Authors a on b.AuthorId = a.Id
     join BookTypes c on b.BookTypeId = c.Id
     where urb.UserId = ?
    group by b.Id
    ) a
	order by " .$order . " " .$type;
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("ss", $user_id, $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //get user read books by id
    public function getUserReadBooksById($user_id, $order, $type) {
    	$query = "select b.id, title, a.name as author, imageURL, urb.dateRead as date ,  c.name as category,
     (select count(*) as nrRatings from BooksRatings
     where bookId = b.Id) as nrRatings,
     (select avg(star) from BooksRatings 
     where BookId = b.id) as rating,
     (select count(*) as nrRead from UsersReadBooks
     where bookId = b.Id) as nrRead
    from Books b join UsersReadBooks urb on
	b.Id = urb.BookId
     join Authors a on b.AuthorId = a.Id
     join BookTypes c on b.BookTypeId = c.Id
     where urb.UserId = ?
     group by b.Id 
	order by " .$order . " " .$type;
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("s", $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //get user wanted books by id
    public function getUserWantedBooksById($user_id, $order, $type) {
    	$query = "select b.id, title, a.name as author, imageURL, urb.dateAdded as date , c.name as category,
     (select count(*) as nrRatings from BooksRatings
     where bookId = b.Id) as nrRatings,
     (select avg(star) from BooksRatings 
     where BookId = b.id) as rating,
     (select count(*) as nrRead from UsersReadBooks
     where bookId = b.Id) as nrRead
    from Books b join UsersWantBooks urb on
	b.Id = urb.BookId
     join Authors a on b.AuthorId = a.Id
     join BookTypes c on b.BookTypeId = c.Id
     where urb.UserId = ?
    group by b.Id
	order by " .$order . " " .$type;
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("s", $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //select category Id and name from DB
    public function getCategories() {
    	$query = "SELECT id, name from BookTypes";
    	$statement = $this->connection->prepare($query);
    	if ($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	} 
    }
    
    //top rated books this week
    
    public function getTopRatedBooksThisWeek() {
    	$query = "SELECT book.id, book.category, book.author, book.title, book.imageURL, max(avg_rating) as thisWeek,
(select count(*) as nrRatings from BooksRatings
     where bookId = book.Id) as nrRatings,
(select avg(star) from BooksRatings 
where BookId = book.Id) as rating 
from
( select b.id, bookTypeId, title, a.name as author, bt.name as category, imageURL, avg(star) as avg_rating
 from BooksRatings br join Books b on b.id = br.bookId
 join BookTypes bt on b.BookTypeId = bt.id
 join Authors a on b.AuthorId = a.id
 where yearweek(br.dateAdded,1) = yearweek(current_date(),1) 
 group by bookId) book
group by bookTypeId";
	$statement = $this->connection->prepare($query);
	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}

    }
    
    //most read books this week
    public function getMostReadBooksThisWeek() {
    	$query = "SELECT book.id, book.category, book.author, book.title, book.imageURL, max(nr) as thisWeek,
(select count(*) from UsersReadBooks urb 
 where urb.BookId = book.Id) as nrRead,
(select avg(star) from BooksRatings 
where BookId = book.Id) as rating 
from
( select b.id, bookTypeId, title, a.name as author, bt.name as category, imageURL, count(*) as nr
 from UsersReadBooks br join Books b on b.id = br.bookId
 join BookTypes bt on b.BookTypeId = bt.id
 join Authors a on b.AuthorId = a.id
 where yearweek(br.dateRead,1) = yearweek(current_date(),1)
 group by bookId) book
group by bookTypeId";
	$statement = $this->connection->prepare($query);
	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    
    //getUserRecomm
     public function getUserRecom($author_id, $category, $user_id) {
    	$query = "select b.id, b.title, a.name as author, bt.name as category, b.imageURL
    from Books b join Authors a on b.AuthorId = a.Id
    join BookTypes bt on b.BookTypeId = bt.Id
	where b.Id in
            (
                    select b.Id from Books b
                where (b.BookTypeId in (select Id from BookTypes where name = ?)
    or b.AuthorId = ?)
    and b.Id not in
            (select urb.bookId from UsersReadBooks urb where urb.UserId = ?)
                and b.Id not in
            (select urb.bookId from UsersWantBooks urb where urb.UserId = ?)
                
      ) order by rand() limit 1 ";
      
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("siss", $category, $author_id, $user_id, $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //get BookInfo by bookId
    public function getBookInfoById($user_id, $book_id) {
    	 $query = "SELECT b.id, b.title, b.imageURL, a.name as author, c.name as category, b.description,
    	    (select count(*) from UsersReadBooks where BookId = ?) as nrRead,
            (select count(*) from UsersReadBooks where UserId = ? and BookId = ?) as readBook,
            (select count(*) from UsersWantBooks where UserId = ? and BookId = ?) as wantBook,
            (select avg(star) from BooksRatings where BookId = ?) as rating,
            (select count(*) from BooksRatings where BookId = ?) as nrRatings,
            (select star from BooksRatings where UserId = ? and BookId = ?) as myRating
    from Books b join Authors a on b.AuthorId = a.Id
    join BookTypes c on b.BookTypeId = c.Id
    where b.Id = ?";
    
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("isisiiisii",$book_id, $user_id,$book_id,$user_id,$book_id,$book_id,$book_id,$user_id, $book_id, $book_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //get Book Reviews
    public function getBookReviews($book_id, $user_id) {
    	$query = "select u.id, u.photo, u.username, r.content as review, r.dateAdded as date from Users u join BooksReviews r on u.id = r.UserId
where r.BookId = ? and u.id != ?";
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("is", $book_id, $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //get Book Ratings by UserId
    public function getBookRatings($book_id, $user_id) {
    	$query = "select u.id, u.photo, u.username, star as rating from Users u join BooksRatings r on u.id = r.UserId
where r.BookId = ? and u.Id != ?";
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("is", $book_id, $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //getUserRating
     public function getUserRating($book_id, $user_id) {
    	$query = "select u.id, u.photo, u.username, star as rating from Users u join BooksRatings r on u.id = r.UserId
where r.BookId = ? and u.id = ?";
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("is", $book_id, $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //getUserReview
    public function getUserReview($book_id, $user_id) {
    	$query = "select u.id, u.photo, u.username, r.content as review, r.dateAdded as date from Users u join BooksReviews r on u.id = r.UserId
where r.BookId = ? and u.id = ?";
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("is", $book_id, $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //getUserInfo
    public function getUserInfo($user_id) {
    	$query = "Select * from Users where Id = ?";
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("s", $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		$statement->close();
    		return $result;
    	}
    }
    
    //getUserInformation
    public function getUserInformation($user_id) {
    	$query = "select *,
(select count(*)  from UsersReadBooks where userId = ?) as nrReadBooks,
(select count(*) from UsersWantBooks where userId = ?) as nrWantedBooks,
(select count(*) from BooksRatings where userId = ?) as nrRatings,
(select count(*) from BooksReviews where userId = ?) as nrReviews
from Users
where Id = ?";

    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("sssss", $user_id, $user_id, $user_id, $user_id, $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		    		$statement->close();
    		return $result;
    	} else {
    		return false;
    	}
    }
    
    //getUsername by Id
    public function getUsernameById($user_id) {
    	$query = "select username from Users
where Id = ?";

    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("s", $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		    		$statement->close();
    		return $result;
    	} else {
    		return false;
    	}
    }
    
    //get Id By username
    public function getIdByUsername($user_id) {
    	$query = "select Id from Users
where username = ?";

    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("s", $user_id);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		    		$statement->close();
    		return $result;
    	} else {
    		return false;
    	}
    }
    
    //edit UserInfo
   public function editUserInfo($user_id, $username, $firstname, $lastname, $birthdate, $location, $gender, $about) {
    	
    	$response = $this->getUsernameById($user_id);
    	$user = $response->fetch_assoc();
    	if ($username != $user["username"])
    	if ($this->userExists($username))
    	return USER_EXISTED;
    	
    	
    	$query = "Update Users
    	set username = ?,
    	firstname = ?,
    	lastname = ?,
    	birthdate = ?,
    	location = ?,
    	gender = ?,
    	about = ?
	where Id = ?";

    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("ssssssss", $username, $firstname, $lastname, $birthdate, $location, $gender, $about, $user_id);
    	if($statement->execute()) {
    		$statement->close();
    		return USER_EDIT_SUCCESS;
    	} else {
    		return 5;
    	}
    }
    
    
    //search Books
    public function searchBooks($string) {
    	$query = "select b.id, title, a.name as author, imageURL,   c.name as category,
     (select count(*) as nrRatings from BooksRatings
     where bookId = b.Id) as nrRatings,
      (select count(*) as nrRead from UsersReadBooks
     where bookId = b.Id) as nrRead,
     (select avg(star) from BooksRatings 
     where BookId = b.id) as rating
    from Books b 
     join Authors a on b.AuthorId = a.Id
     join BookTypes c on b.BookTypeId = c.Id
     where b.title like ? or
	 a.name like ?
   	 group by b.Id";
	

	$string = "%" . $string . "%";
    	$statement = $this->connection->prepare($query);
    	$statement->bind_param("ss", $string, $string);
    	if($statement->execute()) {
    		$result = $statement->get_result();
    		    		$statement->close();
    		return $result;
    	} else {
    		return false;
    	}
    }
    

    //generate ApiKey for User
    private function generateApiKey() {
        return md5(uniqid(rand(), true));
    }

    //generate Unique UserID
    private function generateUniqueId() {
        return uniqid();
    }

}

?>