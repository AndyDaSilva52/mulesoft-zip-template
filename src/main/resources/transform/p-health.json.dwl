%dw 2.0
output application/json skipNullOn="everywhere"
var props = vars.queryParams.'property' default ""
var env = Mule::p('env')
---
{
	name: p('api.name'),
	version: p('api.version'),
	dateTime: now() as String {
		format: "uuuu-MM-dd'T'HH:mm:ss"
	} >> "America/Sao_Paulo" replace '-03:00' with ("")
}

++ {
	env: env,
/**
    muleVersion: mule.version,
	ipAddress: server.ip,
	javaVendor: server.javaVendor,
	javaVersion: server.javaVersion,
*/
}

++ {
	app: {
		groupId   : Mule::p("project.groupId"),
		artifactId: Mule::p("project.artifactId"),
		version   : Mule::p("project.version"),
		packaging : Mule::p("project.packaging")
	}
}


++ (payload default {}) /** Health SpringBootActuator(/health) { "status": "UP|DOWN|UNKNOWN" }*/

++ {
//	( if(env != 'prd') {
        ( if (not isEmpty(props)) (props splitBy(/,/)) map ((prop, idx) -> { ( prop ): p(prop as String) }) else {} )
        /** Properties */
        , ( if (vars.queryParams.'props' as Boolean default false) { props: dw::Runtime::props() } else {} )
//	} else {})
}