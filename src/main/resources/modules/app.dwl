
var cConfigApiName 			= Mule::p('api.name') 
var cConfigApiVersion 		= Mule::p('api.version')

var cDateTimeZone 			= Mule::p('UTC')

var cFileMuleHome			= Mule::p('mule.home') ++ "/apps/" ++ Mule::p('app.name') 
							// "${mule.home}//apps//${app.name}//"
var cFileMuleHomeData		= Mule::p('mule.home') ++ "/apps/" ++ Mule::p('app.name') ++ "/data/"

var cFileWorkingDirectory	= modules::app::cFileMuleHomeData

var cPathIn					= "in/"
var cPathOut				= "out/"

/**
 * 
 */
fun cleanPath(path: String) = do {
	var workingDirectory = modules::app::cFileWorkingDirectory
	
	fun normalize(path: String) = do {
		if( path startsWith "/" )
			(path replace "\\" with "/") replace "//" with "/"
		else
			(path replace "/" with "\\") replace "//" with "\\"
	}
	---
	normalize(path) 
		replace normalize(workingDirectory)
		with ""
}
