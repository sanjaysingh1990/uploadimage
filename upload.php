<?php
 
// Path to move uploaded files
$target_path = "uploadedimages/";
 
// array for final json respone
$response = array();
 
// getting server ip address
$server_ip = gethostbyname(gethostname());
 
// final file url that is being uploaded
$file_upload_url = 'http://' . $server_ip . '/' . $target_path;
 
 
if (isset($_FILES['uploadfile']['name'])) {
    $target_path = $target_path . basename($_FILES['uploadfile']['name']);
 
    // reading other post parameters
   // $email = isset($_POST['email']) ? $_POST['email'] : '';
    //$website = isset($_POST['website']) ? $_POST['website'] : '';
 
    $response['file_name'] = basename($_FILES['uploadfile']['name']);
    //$response['email'] = $email;
    //$response['website'] = $website;
 
    try {
        // Throws exception incase file is not being moved
        if (!move_uploaded_file($_FILES['uploadfile']['tmp_name'], $target_path)) {
            // make error flag true
            $response['error'] = true;
            $response['message'] = 'Could not move the file!';
        }
 
        // File successfully uploaded
        $response['message'] = 'File uploaded successfully!';
        $response['error'] = false;
        $response['file_path'] = $file_upload_url . basename($_FILES['uploadfile']['name']);
    } catch (Exception $e) {
        // Exception occurred. Make error flag true
        $response['error'] = true;
        $response['message'] = $e->getMessage();
    }
} else {
    // File parameter is missing
    $response['error'] = true;
    $response['message'] = 'Not received any file!F';
}
 
// Echo final json response to client
echo json_encode($response);
?>
