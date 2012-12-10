<?php
class MY_Controller extends CI_Controller{
	protected $data = array();

	public function __construct() {
		parent::__construct();
		$this->get_navigation_tree();
	}
	
	function index() {
	}
    private $template = 'default_template';
        
    protected function set_template($view) { 
		$this->template = $view;
	}
    protected function func_display($childdata = NULL){
    	if(! empty($childdata))
        {
            $data = array_merge($this->data,$childdata);
        }
        else
        {
            $data = $this->data;
        }
        $this->load->view($this->template, $data);
    }
    
    protected function get_navigation_tree() {
    	$lang = $this->Categories_model->get_languages();
    	foreach($lang as $key => $language){
    		$lang[$key]["topics"] = $this->Categories_model->get_topics_given_language($language["langID"]);
    	}
    	if(!empty($lang)) {
    		$this->data["cat_tree"] = $lang;
    	}
    }
	protected function is_ajax(){
		return isset($_SERVER['HTTP_X_REQUESTED_WITH']) && strtolower($_SERVER['HTTP_X_REQUESTED_WITH']) == 'xmlhttprequest';	
	}
}
?>