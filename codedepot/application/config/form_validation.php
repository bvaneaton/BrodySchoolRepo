<?php
$config = array(
			 'users/register' => array(
								array(
										'field' => 'posted_data[username]',
										'label' => 'Username',
										'rules' => 'required|min_length[5]|max_length[12]|is_unique[users.username]'
									 ),
								array(
										'field' => 'posted_data[password]',
										'label' => 'Password',
										'rules' => 'trim|required'
									 ),
								array(
										'field' => 'posted_data[passconf]',
										'label' => 'Password Confirmation',
										'rules' => 'required|matches[posted_data[password]]'
									 ),
								array(
										'field' => 'posted_data[email]',
										'label' => 'Email',
										'rules' => 'required|valid_email'
									 ),
								array(
										'field' => 'posted_data[name]',
										'label' => 'Name',
										'rules' => 'required'
									 ),
								array(
										'field' => 'posted_data[webAddress]',
										'label' => 'Web Address',
										'rules' => 'required'
									 )
								),
			 'users/send_message' => array(
								array(
										'field' => 'message_data[username]',
										'label' => 'To',
										'rules' => 'required|match[users.username]'
									 ),
								array(
										'field' => 'message_data[subject]',
										'label' => 'Subject',
										'rules' => 'required'
									 ),
								array(
										'field' => 'message_data[message]',
										'label' => 'Message',
										'rules' => 'required'
									 )
								),
			'submissions/submit_content' => array(
								array(
										'field' => 'submission_data[title]',
										'label' => 'Project Title',
										'rules' => 'required|min_length[3]|max_length[255]'
									),
								array(
										'field' => 'submission_data[language]',
										'label' => 'Language',
										'rules' => 'required'
									 ),
								array(
										'field' => 'submission_data[topic]',
										'label' => 'Topic',
										'rules' => 'required'
									 )
								)
		   );

?>