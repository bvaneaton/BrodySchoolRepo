<?php
class Users_model extends CI_Model {
	
    var $username   = '';
    var $password = '';

    function __construct()
    {
        parent::__construct();
    }
    
    function get_last_ten_entries()
    {
        $query = $this->db->get('users', 10);
        return $query->result();
    }
	
	function get_user($username = "") { 
		$this->db->where('username', $username); 
        $query = $this->db->get("users");
		if ($query->num_rows() > 0) {
            return $query->row_array();
		}else {
			return false;
		}
	}
	
    function insert_user($user_data)
    {
		foreach($user_data as $key => $value){
			if($value == "" && $key != "webAddress"){
				return false;
			}
		}
		$data = array(
			'username' => $user_data["username"],
			'password' => $this->encrypt->encode($user_data["password"]),
			'name' => $user_data["name"],
			'email' => $user_data["email"],
			'webAddress' => ($user_data["webAddress"]) ? $user_data["webAddress"] : ""
		);
        return $this->db->insert('users', $data);
    }

    function update_entry()
    {
        $this->username   = $this->input->post('username');
        $this->password = $this->input->post('password');

        $this->db->update('users', $this, array('username' => $this->input->post('username')));
    }
    
    
    
    /*  ####################
     *  ## Costin's stuff ##
     *  ####################
     *  Function to print basic user info
     *  TODO: proper codeigniter query, make sure these fields exist in the DB
     */
    function printProjects($uid) {
    	// this will print the list of projects with links to them
    }
    
    function printUser($uid) {
    	$result = mysql_query("SELECT * FROM users WHERE username = '$uid'"); // change this to proper codeigniter query
    	 
    	echo "<table border='0'>";
    
    	while ($row = mysql_fetch_array($result)) {
    		echo"<tr>
    		<th class='thc'>Account ID:</th>
   			 <td class='tdc'>";
    		echo $row['username']."</td>  </tr>";
    
    		echo"<tr>
    		<th class='thc'>Name:</th>
   			 <td class='tdc'>";
    		echo $row['name']."</td>  </tr>";
    
    		echo"<tr>
    		<th class='thc'>Website Address:</th>
   			 <td class='tdc'>";
    		echo $row['webAddress']."</td>  </tr>";
    
    		echo"<tr>
    		<th class='thc'>Email:</th>
   			 <td class='tdc'>";
    		echo $row['email']."</td>  </tr>";
    
    		echo"<tr>
    		<th class='thc'>Member Since:</th>
   			 <td class='tdc'>";
    		echo $row['signUpDate']."</td>  </tr>";
    
    		echo"<tr>
    		<th class='thc'>Score:</th>
   			 <td class='tdc'>";
    		echo retScore($uid)."</td>  </tr>";
    	}
    	echo "</table>";
    
    	if (!$result) {
    		$message  = 'Invalid query: ' . mysql_error() . "\n";
    		$message .= 'Whole query: ' . $query;
    		die($message);
    	}
    
    	mysql_free_result($result);
    
    }
    //---- end printUser
    
    function retScore ($uid) {
    	$result = mysql_query("SELECT AVG(Score) AS A FROM submissions WHERE username='$uid' and Score IS NOT NULL");
    	echo mysql_error();
    	if (!$result) {
    		die(" -Failed!");
    	} else {
    		$ret = mysql_fetch_array($result);
    		return $ret['A'];
    	}
    }
    
    //helper functions
    
    function sqlprint ($result, $string) {
    	if (!$result) {
    		die("Query to show fields from table failed");
    	}
    
    	$fields_num = mysql_num_fields($result);
    	echo "<br>";
    	echo $string;
    	echo "<table border='0'><tr>";
    	// printing table headers
    	for($i=0; $i<$fields_num; $i++)
    	{
    		$field = mysql_fetch_field($result);
    		echo "<td class='thca'>{$field->name}</td>";
    	}
    	echo "</tr>\n";
    	// printing table rows
    	while($row = mysql_fetch_row($result))
    	{
    		echo "<tr>";
    
    		// $row is array... foreach( .. ) puts every element
    		// of $row to $cell variable
    		foreach($row as $cell)
    			echo "<td class='tdca'>$cell</td>";
    
    		echo "</tr>\n";
    	}
    	print("</table>\n");
    }
    
    //end Costin's code ------------------>
    
    

}
?>