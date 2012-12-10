<?php if (!defined('BASEPATH')) exit('No direct script access allowed.');
//http://www.michaelwales.com/2010/02/basic-pattern-matching-form-validation-in-codeigniter/
//Found this help from here
class MY_Form_validation extends CI_Form_validation {
  
  // matches_pattern()
  // Ensures a string matches a basic pattern
  // # numeric, ? alphabetical, ~ any character
  public function matches($str, $field)
    {
        
        // Modification of matches array
        $pattern = '/^([a-z_]+)\[(.*)\]$/';
        preg_match($pattern, $field, $matches);

        if (count($matches) > 2) {
            
            
            if ( ! isset($_POST[$matches[1]][$matches[2]]))
            {
                return FALSE;
            }
            
            $field = $_POST[$matches[1]][$matches[2]];

            return ($str !== $field) ? FALSE : TRUE;
            
        }

        
        if ( ! isset($_POST[$field]))
        {
            return FALSE;
        }

        $field = $_POST[$field];

        return ($str !== $field) ? FALSE : TRUE;
    } 
}