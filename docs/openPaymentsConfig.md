```
{
    "alias": "openpayments-test",
    "description": "open payments test plan",
    "server": {
        "paymentPointerUrl": "http://localhost:3000/accounts/gfranklin"
    },
    "consent": {}
}

// Request Grant
{
	"alias": "openpayments-test",
	"description": "open payments test plan",
	"paymentPointer": {
		"paymentPointerUrl": "http://localhost:3000/accounts/gfranklin"
    },
	"paymentPointer2" : {
		"paymentPointerUrl": "http://localhost:4000/accounts/planex"
	},
	"consent": {}
}
```
