<?php

//*****NAME IS THE IMPORTANT LINKING TAG IN HTML TO PHP*************


//This will need to get info about what folder inside files to upload this too
//Probably come from what directory the person is currently looking at on server(IDK)
//When I change this it aint working chief so gotta deal with that
$rootFolderPath ="~/Documents/github/httpServer/root/test/files/";

$filePath = $rootFolderPath . basename($_FILES['file']['name']);
$fileSize = $_FILES['file']['size'];
$fileType = basename($_FILES['file']['type']);
$upload = TRUE;

//echo $filePath;
//echo $fileSize;
//echo $fileType;

if(isset($_POST['submit'])){
	//Check to see if file in path
	if(file_exists($filePath)){
		echo nl2br("File Already Exist\n");
		$upload = FALSE;
	}
	//File size under 8 Megabytes
	if($fileSize > 8000000){
		echo nl2br("File Too Big\n");
		$upload = FALSE;
	}
	//pdf txt jpp png gif jpeg cpp zip java py
	if($fileType != "pdf" && $fileType != "txt" && $fileType != "jpg" && $fileType != "png" 
		&& $fileType != "gif" && $fileType != "jpeg" && $fileType != "x-c++src" && $fileType != "zip" 
		&& $fileType != "x-java" && $fileType != "x-python"){
		
		echo nl2br("Non Supported File Type\n");
		$upload = FALSE;

	}
	//If caught say not uploaded
	if(!$upload){ echo nl2br("File ". $_FILES['file']['name'] ." Not Uploaded\n");}
	
	else{
		if(move_uploaded_file($_FILES['file']['tmp_name'], $filePath)){
			echo nl2br($_FILES['file']['name'] . " was uploaded successfully\n");
		}
		else{ echo nl2br("File ". $_FILES['file']['name'] ." Uploaded Unsuccessfully\n"); }
	}
}

?>
