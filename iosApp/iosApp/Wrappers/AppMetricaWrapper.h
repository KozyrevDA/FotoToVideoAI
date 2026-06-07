//
//  AppMetricaWrapper.h
//
//  Created by msilimon on 18.06.2025.
//  Copyright © 2025 orgName. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface AppMetricaWrapper : NSObject

+ (void)reportEventWithName:(NSString *)name;
+ (void)reportEventWithName:(NSString *)name parameters:(NSDictionary *)parameters;

@end

NS_ASSUME_NONNULL_END
