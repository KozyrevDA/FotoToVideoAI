//
//  FirebaseKMPWrapper.h
//  iosApp
//
//  Created by msilimon on 18.06.2025.
//  Copyright © 2025 orgName. All rights reserved.
//

#import <Foundation/Foundation.h>

NS_ASSUME_NONNULL_BEGIN

@interface FirebaseKMPWrapper : NSObject

+ (void)reportEventWithName:(NSString *)eventName;
+ (void)reportEventWithName:(NSString *)eventName parameters:(NSDictionary<NSString *, id> * _Nullable)parameters;

@end

NS_ASSUME_NONNULL_END
