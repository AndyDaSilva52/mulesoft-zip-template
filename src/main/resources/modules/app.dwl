
var cConfigApiName = Mule::p('api.name') 
var cConfigApiVersion = Mule::p('api.version')

var cDateTimeZone = Mule::p('UTC')

var cFileMuleHome = Mule::p('mule.home') ++ "/apps/" ++ Mule::p('app.name') 
							// "${mule.home}//apps//${app.name}//"
var cFileMuleHomeData = Mule::p('mule.home') ++ "/apps/" ++ Mule::p('app.name') ++ "/data/"

var cFileWorkingDirectory = modules::app::cFileMuleHomeData

var cPathIn = "in/"
var cPathOut = "out/"


/**
* Describes the `cleanPath` function purpose.
*
* === Parameters
*
* [%header, cols="1,1,3"]
* |===
* | Name | Type | Description
* | `path` | String | 
* |===
*
* === Example
*
* This example shows how the `cleanPath` function behaves under different inputs.
*
* ==== Source
*
* [source,DataWeave,linenums]
* ----
* %dw 2.0
* output application/json
* ---
*
*
* ----
*
* ==== Output
*
* [source,Json,linenums]
* ----
*
* ----
*
*/
fun cleanPath(path: String): String = do {
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
