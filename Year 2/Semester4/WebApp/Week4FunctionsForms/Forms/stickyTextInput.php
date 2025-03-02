<?php
// -----------------------------------------------------------------------------
// This version generates the form and prcessess the form.
// If the submit button is undefined then the form is displayed
// else the form is processed.
// Error checking is also performed on the names
// The form is also sticky: previous values are shown for error correction
// -----------------------------------------------------------------------------

echo "<u>Forms with ERROR CHECKING & STICKY INPUT on TEXT BOXES </u> <p>";

if (isset($_REQUEST['button'])) // submit was clicked
{
   process_form();
}
else // display form for first time
{
   display_form_page('');
}
?>

<?php
// Display the form page and the error message
// An empty error message indicates valid data
function display_form_page($error)
{
   $self = $_SERVER['PHP_SELF'];
   $first_name = isset($_REQUEST['firstname']) ? $_REQUEST['firstname'] : '';
   $last_name = isset($_REQUEST['lastname']) ? $_REQUEST['lastname'] : '';
?>
<html>
<head>
<link rel="stylesheet" type="text/css" href="input.css"/>
<title>Forms, version 4</title>
<style type="text/css">
  .error { color: #ff0000 }
</style>
</head>
<body>
<h1>Forms, Version 4</h1>

In this version the form is generated by the PHP script
using error checking and sticky input fields.

<?php
if ($error)
{
   echo "<p>$error</p>\n";
}
?>

<form action="<?php echo $self ?>" method="POST">
<p>First  name: <input class="input" type="text" name="firstname"
   value="<?php echo $first_name?>"></p>
<p>Last name: <input class="input" type="text" name="lastname"
   value="<?php echo $last_name?>"></p>
<p><input type="submit" name="button" value="Submit Name"></p>
</form>

</body>
</html>
<?php
}
?>


<?php
function process_form()
{
   $error = validate_form();
   if ($error)
   {
      display_form_page($error);
   }
   else
   {
      display_output_page();
   }
}
?>


<?php
// Return an error string that is empty if there were no errors.
// Otherwise it contains an error message.
function validate_form()
{
   $first_name = trim($_REQUEST['firstname']);
   $last_name = trim($_REQUEST['lastname']);

   $error = '';

   // Our definition of a valid name is one containing letters, hyphens,
   // and apostrophe's

   $reg_exp = '/^[a-zA-Z\-\']+$/';

   if (! preg_match($reg_exp, $first_name))
   {
      $error .= "<span class=\"error\">First name is invalid (letters, hyphens, ', only)</span><br>";
   }
   if (! preg_match($reg_exp, $last_name))
   {
       $error .= "<span class=\"error\">Last name is invalid (letters, hyphens, ', only)</span><br>";
   }
   return $error;
}
?>


<?php
function display_output_page()
{
   $first_name = trim($_REQUEST['firstname']);
   $last_name = trim($_REQUEST['lastname']);
?>
   <html>
   <head><title>Form Results</title></head>
   <body>
   <h1>Form Results</h1>
   <?php echo "Hello $first_name $last_name<br/>\n"; ?>
   </body>
   </html>
<?php
}
?>

