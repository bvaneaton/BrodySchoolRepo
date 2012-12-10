<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Submissions extends MY_Controller {
	
	function __construct()
	{
		parent::__construct();
		$this->load->helper('directory');
		$this->load->helper('file');
		$this->load->helper('download');
	}
	
	function index()
	{	
		$content_data['languages'] = $this->Languages_model->get_all_languages();
		$content_data['topics'] = $this->Topics_model->get_all_topics();
		$content_data['subtopics'] = $this->Subtopics_model->get_subtopics();
		$this->load->view('main/submit', $content_data);
	}
	
	function view_submissions($username){
		$data['submissions'] = $this->Submissions_model->get_submission_given_username_all($username);
		$this->load->view('main/all_submissions_form', $data);
	}
	
	function show_form($submissionID, $form){		
		$data['submissionID'] = $submissionID;
		$this->load->view('main/'.$form, $data);
	}
	
	function view_categories()
	{
		$content_data['title'] = "Welcome";
		$data['content'] = $this->load->view('main/welcome', $content_data,TRUE);
		$this->func_display($data);
	}
	
	function submit_comment($submissionID){
		$posted_data = $this->input->post('comment_text');
		$result = $this->Submissions_model->get_submission_given_submissionID($submissionID);
		$data = array('submissionID' => $result['submissionID'], 'comment' => $posted_data, 'username' => $this->session->userdata('username'));
		$this->Comments_model->insert_comment($data);
		redirect("/users/detailedSubmit/".$result['submissionID'], "REFRESH");
	}
	
	function submit_description($submissionID){
		$posted_data = $this->input->post('submission_description');
		$result = $this->Submissions_model->get_submission_given_submissionID($submissionID);
		$data = array('submissionID' => $submissionID, 'description' => $posted_data);
		$this->Submissions_model->insert_description($data);
		redirect("/users/detailedSubmit/".$result['submissionID'], "REFRESH");
	}
	
	function submit_screenshots($submissionID){
		$data = directory_map('./uploads/');
		$result = $this->Submissions_model->get_submission_given_submissionID($submissionID);
		if(!is_null($data)) {
			foreach ($data as $key => $folder) {
				if ($this->encrypt->decode(base64_decode($key)) == $this->session->userdata('username')){
					$config['upload_path'] = ('uploads/SS');
					break;
				}
				else {
					$config['upload_path'] = ('uploads/black_hole_files');
				}
			}
		};
		
		$config['allowed_types'] = 'jpg|jpeg';
		$config['max_size']	= '300000';
		$config['max_width']  = '3024';
		$config['max_height']  = '3768';
		$config['overwrite'] = false;
		$config['file_name'] = str_replace(" ", "_", $this->session->userdata('username').'_'.$result['submissionName']);
		$config['remove_spaces'] = true;
		
		$this->load->library('upload', $config);
			
		if ( ! $this->upload->do_upload())
		{
			redirect("/home", "REFRESH");
		}
		else
		{
			$screenData = json_decode($result['screenshots']);
			$names = get_filenames($config['upload_path']);
			if ($screenData == null){				
				$fileName = check_file_name($this->session->userdata('username'), $result['submissionName'], $names);
				$screenData[0] = $config['upload_path'].'/'.$fileName;
			}
			else
			{
				$count = count($screenData);
				$fileName = check_file_name(($this->session->userdata('username')), $result['submissionName'], $names, $count);
				array_push($screenData,  $config['upload_path'].'/'.$fileName);
			}
			$screenDataJson = json_encode($screenData);
			$this->Submissions_model->submit_image($result['submissionID'], $screenDataJson);
			redirect("/users/detailedSubmit/".$result['submissionID'], "REFRESH");
		}
	}
	
	function remove_comment($commentID, $submissionID){
		$this->Comments_model->remove_comment($commentID);
		redirect("/users/detailedSubmit/".$submissionID, "REFRESH");
	}
	
	function download_submission($submissionID){
		$result = $this->Submissions_model->get_submission_given_submissionID($submissionID);
		
		$data = directory_map('./uploads/');
		foreach ($data as $key => $folder) {
			if ($this->encrypt->decode(base64_decode($key)) == $result['username']){
				$names = get_filenames('./uploads/'.$key.'/'.$result['submissionName']);
				$data = file_get_contents('./uploads/'.$key.'/'.$result['submissionName'].'/'.$names[0]);
			}
		}				
		force_download($result['submissionName'].'.rar', $data);
		
	}
	
	function delete_submission($submissionID, $select){
		if (!($select == 0)){
			$result = $this->Submissions_model->get_submission_given_submissionID($submissionID);
			$json_result = json_decode($result['screenshots']);	
				
			foreach ($json_result as $key => $path) {
				if (file_exists($path)) {
					unlink($path) or die('failed deleting: ' . $path);
				}
			}
			
			$data = directory_map('./uploads/');
			if(!is_null($data)) {
				foreach ($data as $key => $folder) {
					if ($this->encrypt->decode(base64_decode($key)) == $result['username']){
						delete_files('./uploads/'.$key.'/'.$result['submissionName']);
						rmdir('./uploads/'.$key.'/'.$result['submissionName']);
					}
				}
			}
			$this->Submissions_model->remove_entry($submissionID);
			redirect("/users/index/".$this->session->userdata('username'), "REFRESH");
		}
		redirect("/users/detailedSubmit/".$submissionID, "REFRESH");
	}
	
	
	function submit_content(){
		$posted_data = $this->input->post('submission_data');
		if($this->form_validation->run() != FALSE){	
			$data = directory_map('./uploads/');
			if(!is_null($data)) {
				foreach ($data as $key => $folder) {
					if ($this->encrypt->decode(base64_decode($key)) == $this->session->userdata('username')){
						mkdir('./uploads/'. $key. '/'. $posted_data["title"] , 0777);
						if (!(file_exists('./uploads/SS'))){
							mkdir('./uploads/SS' , 0777);
						}

						$config['upload_path'] = ('./uploads/'. $key.'/'. $posted_data["title"]);
						break;
					}
					else {
						$config['upload_path'] = ('./uploads/black_hole_files');					
					}
				}
			}
			$config['allowed_types'] = 'rar';
			$config['max_size']	= '1000000';
			$config['file_name'] = str_replace(" ", "_", $posted_data["title"]);

			if (!($posted_data["overwrite"]) == null){
				$config['overwrite'] = true;
			}
			else{
				$config['overwrite'] = false;
			}
			$this->load->library('upload', $config);
			
			if ( ! $this->upload->do_upload())
			{
				redirect("/home", "REFRESH");
			}
			else
			{
				$data = array('upload_data' => $this->upload->data());
				
				$dataArray['subtopic1'] = null;
				$dataArray['subtopic2'] = null;
				$dataArray['subtopic3'] = null;
				
				if ($posted_data['topic'] == 0){
					redirect("/home", "REFRESH");
				}
				else
				{
					if (!($this->Categories_model->category_exists($posted_data["language"], $posted_data["topic"]))){
						$this->Categories_model->insert_entry(array ('langID' => $posted_data["language"], 'topicID' => str_replace(" ", "_",$posted_data["topic"])));
					} 
					$data['contentID'] = $this->Categories_model->get_category_with_langTopic($posted_data["language"], str_replace(" ", "_",$posted_data["topic"]));
	
					if (!($this->Submissions_model->check_if_exists($this->session->userdata('username'), str_replace(" ", "_",$posted_data["title"])))){
						$this->Submissions_model->insert_entry(array ('submissionName' => str_replace(" ", "_", $posted_data["title"]),'categoryID' => $data['contentID']['categoryID'], 'username' => $this->session->userdata('username')));
						$data['submissionContent'] = $this->Submissions_model->get_submission_given_user_subname($this->session->userdata('username'), str_replace(" ", "_",$posted_data["title"]));		
					
						for ($i = 0; $i < count($posted_data['subtopics']); $i++){
							if ($i > 3){
								break;
							}
							$dataArray['subtopic'.($i+1)] = $posted_data['subtopics'][$i];				
						}				
						
						$this->Tags_model->insert_entry($data['submissionContent']['submissionID'], $dataArray);
					}
					else if (($this->Submissions_model->check_if_exists($this->session->userdata('username'), str_replace(" ", "_", $posted_data["title"]))) && $config['overwrite'] == false){
						$i = 1;
						while($this->Submissions_model->check_if_exists($this->session->userdata('username'), str_replace(" ", "_", $posted_data["title"].$i))){
							$i++;
						}
						
							$this->Submissions_model->insert_entry(array ('submissionName' => str_replace(" ", "_", $posted_data["title"].$i),'categoryID' => $data['contentID']['categoryID'], 'username' => $this->session->userdata('username')));
							$data['submissionContent'] = $this->Submissions_model->get_submission_given_user_subname($this->session->userdata('username'), str_replace(" ", "_", $posted_data["title"].$i));
								
							for ($j = 0; $j < count($posted_data['subtopics']); $j++){
								if ($j > 3){
									break;
								}
								$dataArray['subtopic'.($j+1)] = $posted_data['subtopics'][$j];
							}
								
							$this->Tags_model->insert_entry($data['submissionContent']['submissionID'], $dataArray);
						
					}
					else{
						$this->Submissions_model->update_entry($this->session->userdata('username'), str_replace(" ", "_",$posted_data["title"]), $data['contentID']['categoryID']);
					}
					redirect("/users/index/".$this->session->userdata('username'), "REFRESH");
				}
			}
		}
		else {
			$this->load->view('main/submit');
		}
	}
	
	function vote(){
		if($this->session->userdata("username")){
			$vote = $this->input->post('vote');
			$submissionId = $this->input->post('submissionId');
			$result = $this->Submissions_model->insert_vote($submissionId, ($vote)?"voteUp":"voteDown");
			echo "Successfully Voted";
		} else {
			echo "<span class='warning'>Please login to vote</span>";	
		}
	}
	
	function display_votes(){
		$submissionId = $this->input->get('submissionId');
		$result = $this->Submissions_model->getScore($submissionId);
		$result["html"] = $this->load->view('main/vote_bar',array("vote_data" => $result),true);
		echo json_encode($result);
	}
}
?>