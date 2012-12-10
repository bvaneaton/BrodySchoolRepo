<?php
class Categories_model extends CI_Model {

    var $categoryid = '';
    var $langID = '';
    var $topicID = '';       
    
    function __construct()
    {    	
        parent::__construct();
    }
    
    function insert_entry($cat_data)
    {
       $data = array(
			'langID' => $cat_data["langID"],
			'topicID' => $cat_data["topicID"]
		);
        return $this->db->insert('categories', $data);
    }
    
    function category_exists($langID = "", $topicID = "")
    {
    	$this->db->where('langID', $langID);
    	$this->db->where('topicID', $topicID);
    	$query = $this->db->get('categories');
    	if ($query->num_rows() > 0){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    
    function get_category_with_langTopic($langID = "", $topicID = "")
    {
    	$this->db->where('langID', $langID);
    	$this->db->where('topicID', $topicID);
    	$query = $this->db->get('categories');
    	if ($query->num_rows() > 0){
    		return $query->row_array();
    	}
    	else{
    		return false;
    	}
    }
	
	function get_category($categoryID = "") { 
		$this->db->where('categoryID', $categoryID); 
        $query = $this->db->get("categories");
		if ($query->num_rows() > 0) {
            return $query->row_array(); 
		}else {
			return false;
		}
	}
	
	function get_languages($langID = "") {
		$result = $this->Languages_model->get_all_languages();
		if ($result){
			return $result;
		}else {
			return false;
		}
	}
	
	function get_topics($topicID = "") {
		$result = $this->Topics_model->get_all_topics();
		if ($result){
			return $result;
		}else {
			return false;
		}
	}
	
	function get_topics_given_language($langID = "") {
		/*$this->db->select('`topics_categories.topicID`, topic_name');
		$this->db->where('`categories.topicID`','`topics_categories.topicID`');
		$this->db->where('languageID', $languageID);
		$this->db->from('categories, topics_categories');
		$query = $this->db->get();*/
		$query = $this->db->query('SELECT topics_categories.topicID, topic_name
						FROM (categories, topics_categories)
						WHERE categories.topicID = topics_categories.topicID
						AND langID = ' .$langID);
		if ($query->num_rows() > 0) {
			return $query->result_array();
		}else {
			return false;
		}
	}
	
	function get_language_given_topic($topicID = "") {
		$query = $this->db->query('SELECT language_categories.langID, lang_name
						FROM (categories, language_categories)
						WHERE categories.langID = language_categories.langID
						AND topicID = ' .$topicID);
		if ($query->num_rows() > 0) {
			return $query->result_array();
		}else {
			return false;
		}
	}
	
	function get_category_given_topicID($topicID = "") {
		$this->db->where('topicID', $topicID);
		$query = $this->db->get("categories");
		if ($query->num_rows() > 0) {
			return $query->result_array();
		}else {
			return false;
		}
	}
	
	
	// this function is used for the right bar menu and will resturn a list of tags based on language and topic.
	function get_tags_with_langTopic($lang = "", $topic = "")
	{
		$this->db->where('langID', $langID, 'topicID', $topicID);
		$query = $this->db->get('categories');
		if ($query->num_rows() > 0){
			return $query->row_array();
		}
		else{
			return false;
		}
	}
	
	//<------- browse functions -->
	//return entries by language
	function get_submissions($where_clause) {
		$this->db->order_by("score", "desc"); 
		$this->db->where($where_clause);
		$post = $this->input->post();
		if(isset($post["tagid"])) {
			$where_filter = "(tags.subtopic1 = ".$post["tagid"]." OR tags.subtopic2 = ".$post["tagid"]." OR tags.subtopic3 = ".$post["tagid"].")";
			$this->db->where($where_filter);
		}
		$this->db->from("submissions",false);
		$this->db->join('categories', 'categories.categoryID = submissions.categoryID','left',false);
		$this->db->join('language_categories', 'language_categories.langID = categories.langID','left',false);
		$this->db->join('topics_categories', 'topics_categories.topicID = categories.topicID','left',false);
		$this->db->join('tags', 'tags.submissionID = submissions.submissionID','left',false);
		$this->db->select("",false);

		$query = $this->db->get();
		if ($query->num_rows() > 0) {
			$data["submissions"] = $query->result_array();
		}else {
			return false;
		}
		

		foreach ($data["submissions"] as $key => $value){
			$ids[] = $value["submissionID"];
		}
		$data["tags_list"] = $this->get_unique_subtopics($ids);
		return $data;
	}
	function get_unique_subtopics($subtopics_array) {
		$query = $this->db->query('
			SELECT DISTINCT subtopic1,subtopicName FROM (
				SELECT distinct subtopic1, tags.submissionID, subtopicName
				FROM (submissions) 
				LEFT JOIN tags ON tags.submissionID = submissions.submissionID
				LEFT JOIN subtopics_categories a ON a.subtopicID = tags.subtopic1
				UNION ALL
				SELECT distinct subtopic2, tags.submissionID, subtopicName
				FROM (submissions) 
				LEFT JOIN tags ON tags.submissionID = submissions.submissionID
				LEFT JOIN subtopics_categories a ON a.subtopicID = tags.subtopic2
				UNION ALL
				SELECT distinct subtopic3, tags.submissionID, subtopicName
				FROM (submissions) 
				LEFT JOIN tags ON tags.submissionID = submissions.submissionID
				LEFT JOIN subtopics_categories a ON a.subtopicID = tags.subtopic3) subtopics
			WHERE submissionID IN ('.implode(',',$subtopics_array).') AND subtopic1 IS NOT NULL
		');
		return $query->result_array();
	}
	function get_entries_by_lang($lang="")
	{
		$query = $this->db->query('
				SELECT s.SubmissionID
				FROM (categories AS c, language_categories AS lc, submissions AS s)
				WHERE c.categoryID = s.categoryID AND c.langID = lc.langID
				AND lc.lang_name = '."'".$lang."'"
		);
		return $query->result_array();
	}
	//return submissions based on language name and topic
	function get_entries_by_lang_topic($lang="", $topic="")
	{
		$query = $this->db->query('
				SELECT s.SubmissionID
				FROM (categories AS c, language_categories AS lc, topics_categories AS tc, submissions AS s)
				WHERE c.categoryID = s.categoryID AND c.langID = lc.langID AND c.topicID = tc.topicID
				AND lc.lang_name = '."'" .$lang. "'".' AND tc.topic_name ='."'".$topic."'"
		);
		return $query->result_array();
	}
	//return submission ID and Tags
	function get_submission_and_tags($submissionID = "") {
		$query = $this->db->query('
				SELECT t.submissionID, sc.subtopicName AS tag1, sc2.subtopicName AS tag2, sc3.subtopicName AS tag3
				FROM tags AS t
				LEFT JOIN subtopics_categories AS sc ON t.subtopic1 = sc.subtopicID
				LEFT JOIN subtopics_categories AS sc2 ON t.subtopic2 = sc2.subtopicID
				LEFT JOIN subtopics_categories AS sc3 ON t.subtopic3 = sc3.subtopicID
				WHERE t.submissionID ='.$submissionID
		);
		return $query->result_array();
	}
	
	
	
	//this function creates an array of IDs and tags
	function return_subtag_array($idArray ="") {
		foreach($idArray as $key => $val) {
			$subtags[$val["SubmissionID"]] = $this->get_submission_and_tags($val["SubmissionID"]);
		}
		return $subtags;
	}
	
	//return a list of used UNIQUE tags from a subtag_array
	function return_unique_subtags ($subtags = "") {
		
	}

	
	//this function takes an entry ID array and return an array with the data entries for the IDs
	function return_entry_array($idArray ="") {
		foreach($idArray as $key => $val) {
			$submissions[$val["SubmissionID"]] = $this->Submissions_model->get_submission_given_submissionID($val["SubmissionID"]);
		}
		return $submissions;
	}
	
	
	function filter_results_by_tag ($array, $tag) {
		//todo
	}
}
?>