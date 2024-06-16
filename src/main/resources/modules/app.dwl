
var cConfigApiName 			= Mule::p('api.name')
var cConfigApiVersion 		= Mule::p('api.version')

var cDateTimeZone 			= Mule::p('UTC')

var cFileMuleHome			= Mule::p('mule.home') ++ "/apps/" ++ Mule::p('app.name')
							// "${mule.home}//apps//${app.name}//"
var cFileMuleHomeData		= Mule::p('mule.home') ++ "/apps/" ++ Mule::p('app.name') ++ "/data/"

var cPathIn					= "in/"
var cPathOut				= "out/"
