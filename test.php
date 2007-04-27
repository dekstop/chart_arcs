<?php

require_once('XPath.class.php');

$data = array(
	array(
		'from' => 1159099200,
		'to' => 1159704000,
		'entries' => array( 'entry1', 'entry2', 'entry3', 'entry4' )
	),
	array(
		'from' => 1,
		'to' => 2,
		'entries' => array( 'entry1', 'entry2', 'entry3', 'entry4' )
	),
	array(
		'from' => 1,
		'to' => 2,
		'entries' => array( 'entry1', 'entry2', 'entry3', 'entry4' )
	)
);

$xPath =& new XPath('', array(XML_OPTION_SKIP_WHITE => TRUE));
#if (!$xPath->importFromString($xmlSource)) { echo $xPath->getLastError(); exit; }

$rootnode = $xPath->appendChild('', '<user/>');
$xPath->setAttribute($rootnode, 'name', 'martind');

foreach ($data as $chart) {
	$chartnode = $xPath->appendChild($rootnode, '<chart/>');
	$xPath->setAttribute($chartnode, 'from', $chart['from']);
	$xPath->setAttribute($chartnode, 'to', $chart['to']);
	
	foreach ($chart['entries'] as $entry) {
		$entrynode = $xPath->appendChild($chartnode, '<entry/>');
		$xPath->setAttribute($entrynode, 'name', $entry);
		#$xPath->setAttribute($entrynode, 'name', 'The Chess Cadet Allstars');
		#$xPath->setAttribute($entrynode, 'counter', '60');
	}
}

#$xPath->reindexNodeTree();
echo $xPath->exportAsXml();

?>