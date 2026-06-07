//
//  AppMetricaWrapper.m
//  iosApp
//
//  Created by msilimon on 18.06.2025.
//  Copyright © 2025 orgName. All rights reserved.
//

#import "AppMetricaWrapper.h"
@import AppMetricaCore;

@implementation AppMetricaWrapper

+ (void)reportEventWithName:(NSString *)name {
    [AMAAppMetrica reportEvent:name onFailure:nil];
}

+ (void)reportEventWithName:(NSString *)name parameters:(NSDictionary<NSString *, id> *)parameters {
    [AMAAppMetrica reportEvent:name parameters:parameters onFailure:nil];
}

@end
