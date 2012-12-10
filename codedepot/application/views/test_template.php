<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title>CodeDepot</title>
<?php $this->load->view('template/service_head.php'); ?>
</head>

<body>
<div id="page-container">
	<?php $this->load->view('test/header.php'); ?>
    <div id="content-container">
        <div id="center">
        	<?php echo $content; ?>
        </div>
        <?php if (isset($right_bar)) {?>
            <div id="right-bar">
                <?php echo $right-bar ?>
            </div>
        <?php } ?>
    </div>
    <?php $this->load->view('template/footer.php'); ?>
</div>
</body>
</html>