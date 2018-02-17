//
//  Message.h
//  NOS_SecondLab
//
//  Created by Filip Gulan on 20/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import <Foundation/Foundation.h>
#import "Timestamp.h"

@interface Message : NSObject

@property (nonatomic, strong, readonly) Timestamp *timestamp;
@property (nonatomic, copy, readonly) NSString *text;
@property (nonatomic, strong, readonly) NSString *serialized;

+ (instancetype)new NS_UNAVAILABLE;
- (instancetype)init NS_UNAVAILABLE;
- (instancetype)initWithText:(NSString *)text timestamp:(Timestamp *)timestamp;
+ (instancetype)messageWithText:(NSString *)text timestamp:(Timestamp *)timestamp;
+ (instancetype)messageFromString:(NSString *)string;

@end
