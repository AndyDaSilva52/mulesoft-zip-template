

fun set(outboundHeaders: Any, correlationId: Any) = do {
	var keyCorrelationId = 'X-Correlation-ID'
	var includesHeaderCorrelationId = true /** Mule::p('api.response.headers.xCorrelationId')  */
	---
	if ( includesHeaderCorrelationId )
        ((
            (outboundHeaders default {}) as Object mapObject ((value, key, index) -> (lower(key)): value)
        ) - lower(keyCorrelationId))
        ++ ({ (keyCorrelationId): correlationId })
	else
		/** APIKit Default */
		(outboundHeaders default {})
}

