<?php

    // get weekly charts history for a resource
    
    // martind 2006-10-07, 14:22:29

    require_once('/web/site/www.staging.last.fm/app/init.php');

    $dbm = new DBManager();
    
    if (count($_SERVER['argv']) < 2) {
        print "Parameters: <username>\n";
        exit;
    }

    $username = $_SERVER['argv'][1];
    
    $numcharts = 26;
    $chartsize = 50;

    $user = User::getByName($username);
    $charts =& ASCharts::getForResource($user->_restype, $user->id);

    $doc = domxml_new_doc('1.0');
    
    $rootnode = $doc->append_child($doc->create_element('resource'));
    $rootnode->set_attribute('name', $user->name);
    $rootnode->set_attribute('restype', $user->_restype);
    $rootnode->set_attribute('id', $user->id);
    
    $weeks = $charts->getWeeksData();
    
    #$week = array_pop($weeks);

    foreach (array_slice($weeks, - $numcharts) as $week) {
    
        $chartnode = $rootnode->append_child($doc->create_element('chart'));
        $chartnode->set_attribute('from', $week['from']);
        $chartnode->set_attribute('to', $week['to']);
        
        $entries_reslist = $charts->getWeeklyArtists($chartsize, $week['range']);
        $entries_reslist->setGetMode(RESOURCELIST_GETMODE_SIMPLELIST);

        while($li =& $entries_reslist->next()){
            
            $id = $li['id'];
            $counter = $li['counter'];

            $artist = Artist::getByID($id);
            
            $entrynode = $chartnode->append_child($doc->create_element('entry'));
            $entrynode->set_attribute('name', $artist->name);
            $entrynode->set_attribute('reach', $artist->getReach());
            $entrynode->set_attribute('counter', $counter);
        }            
    }
    
    print $doc->dump_mem(true, 'UTF-8');
    
    // print count($weeks) . " weeks of data.\n";
    // 
    // $lastweek = array_pop($weeks);
    // if($lastweek) {
    //         print_r($lastweek);
    //     $artists = $charts->getWeeklyArtistsData(10, $lastweek['range']);
    //     //to get the objects do this
    //     //$artists = $charts->getWeeklyArtists(10, $lastweek['range']);
    //     print_r($artists);
    // }
?>