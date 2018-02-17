//
//  Timestamp.m
//  NOS_SecondLab
//
//  Created by Filip Gulan on 20/03/2017.
//  Copyright © 2017 Filip Gulan. All rights reserved.
//

#import "Timestamp.h"

@interface Timestamp ()

@property (nonatomic, assign) NSInteger sourceID;
@property (nonatomic, assign) NSInteger value;

@end

@implementation Timestamp

- (instancetype)initWithSourceID:(NSInteger)sourceID value:(NSInteger)value
{
    self = [super init];
    if (self) {
        _sourceID = sourceID;
        _value = value;
    }
    return self;
}

- (BOOL)isEqual:(id)other
{
    if (other == self) {
        return YES;
    } else if (![other isKindOfClass:Timestamp.class]) {
        return NO;
    } else {
        Timestamp *stamp = (Timestamp *)other;
        return self.sourceID == stamp.sourceID && self.value == stamp.value;
    }
}

- (NSUInteger)hash
{
    return self.value ^ self.sourceID;
}

@end
