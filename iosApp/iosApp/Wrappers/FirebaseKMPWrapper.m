//
//  FirebaseKMPWrapper.m
//  iosApp
//
//  Created by msilimon on 18.06.2025.
//  Copyright © 2025 orgName. All rights reserved.
//

#import "FirebaseKMPWrapper.h"
@import FirebaseAnalytics;

@implementation FirebaseKMPWrapper

+ (void)reportEventWithName:(NSString *)eventName {
    [FIRAnalytics logEventWithName:eventName parameters:nil];
}

+ (void)reportEventWithName:(NSString *)eventName parameters:(NSDictionary<NSString *, id> * _Nullable)parameters {
    [FIRAnalytics logEventWithName:eventName parameters:parameters];
}

@end
