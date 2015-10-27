package com.meijialife.simi.alipay;

/*
 * Copyright (C) 2010 The MobileSecurePay Project
 * All right reserved.
 * author: shiqun.shi@alipay.com
 *
 *  提示：如何获取安全校验码和合作身份者id
 *  1.用您的签约支付宝账号登录支付宝网站(www.alipay.com)
 *  2.点击“商家服务”(https://b.alipay.com/order/myorder.htm)
 *  3.点击“查询合作者身份(pid)”、“查询安全校验码(key)”
 */
//
// 请参考 Android平台安全支付服务(msp)应用开发接口(4.2 RSA算法签名)部分，并使用压缩包中的openssl RSA密钥生成工具，生成一套RSA公私钥。
// 这里签名时，只需要使用生成的RSA私钥。
// Note: 为安全起见，使用RSA私钥进行签名的操作过程，应该尽量放到商家服务器端去进行。
public final class Keys {

	// 合作身份者id，以2088开头的16位纯数字
	public static final String DEFAULT_PARTNER = "2088911704798349";

	// 收款支付宝账号
	public static final String DEFAULT_SELLER = "meijialife@126.com";

	// 商户私钥，自助生成
	public static final String PRIVATE = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOqA+tDx0dtH/aZ5vfO9fM/r23HJuwYDlzHFSW7D2v0ggr68q26hPBuMtIhBH+QpZbBSCXcuITLRwpU3zbMNo3h8fFMWT8zcYwyG0Va7J69IMuu8PxNdfXY1Pw2rtRe/oVpBuPySK4dsBQdP+on1VXY+q3sPcFDvebnrpOv1Z8apAgMBAAECgYEAsE0NxD65jkleamVGqNPB3TOuBKssX0Ydyn5PSeYXYoypnyqLHB496Vxsca+8gTWaTOgwU5irvuAykSqmDEJprYAc8QOJ+dVoh5nE5dCXKr4sG7fEpB3h4kUJj3SGJNEMwS3WaU5oDibPGDBsSRUKexrKgQq81OomPdmM2r1p2d0CQQD8fouhETZ54j+f0IcEw9YYkTI2w8U9lHIsb3hreGt2aOMeKuFLuNMbwrJDe6g2uJJk5nJSAmA0cKTOCrI0ODirAkEA7cJ9XoYESIsShh7cT0PJ8fV7FwH20VMOHAE/JX3Iz0ic02hT1PiN8Y4IzGX51z41AcYYTiBtPI9DSUOPFUal+wJAd5tmf10NBj3Dz+vTGdG/KJIXTFP+qkYYs632SEqZ9VSISfEcGmeqMVtQYKFCNPHH/14ex3RyqES3/RaaBhyf0wJAHVl21GZCvRm9zaSdmYNKuH2PFkX9y9Uu1rIos14VndDe2xHz10RfcUQRPfoQn0tki7WCZpKGKYWdzrtyPDQeZQJBAPg3YIBsxiYz5LbndMqrjtynWJNKnTpIb4xb8V+GugVZ8QVBNlRF0G690AHAuQ1qYLJ131VCdRuC2oh/4GF1ss8=";
	public static final String PUBLIC = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCnxj/9qwVfgoUh/y2W89L6BkRAFljhNhgPdyPuBV64bfQNN1PjbCzkIM6qRdKBoLPXmKKMiFYnkd6rAoprih3/PrQEB/VsW8OoM8fxn67UDYuyBTqA23MML9q1+ilIZwBC2AQ2UBVOrFXfFl75p6/B5KsiNG9zpgmLCUYuLkxpLQIDAQAB";
}
