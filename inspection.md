# Code inspection procedure
## Logistics:

1. When will a formal code inspection be warranted?
   1. Before submitting a milestone
2. Who will take the lead on moderating inspections?
   1. We will dynamically assign roles depending on who wrote the code in question
3. How will you share the results of inspections?
   1. Via Microsoft Teams


## Criteria:

4. What are the key "code smells" from each chapter of Clean Code? This is the big question. Chapter 17 of Clean Code might help you recall them.
   1. G5: Duplication - Nicco
   2. F2: Output arguments - Ian
   3. G16: Obscured intent - Ian
   4. C2: Obsolete Comment - Will
   5. J3: Constants vs Enums - Aaron
   6. N4: Unambiguous names - Nicco
   7. T5: Test boundary conditions - Aaron
   8. E2: Tests require more than one step - Will
5. Will everyone apply all criteria from every chapter from Clean Code? Or will each person specialize in a few criteria?
   1. It would be more effective for each person to focus on a few
   2. However, all member will at least become familiar with all criteria

## Scope:

6. Will your team inspect every file in your codebase? Every file you touch in your feature branch? Or something else entirely?
   1. Objects which have particularly complex functionality will be inspected

7. Of those files, will each person look at every file in consideration? Or will your team assign different files to different people?
   1. Each person will inspect each file, looking specifically for their assigned criteria

## Tools:
8. To what extent can your inspection criteria be automated? Automation will increase your inspection's speed and reliability.
   1. Many of testing "code smells" can be automated by writing good unit test
   2. IntelliJ's auto formatter can be configured to apply out formatting criteria
9. Which aspects of your inspection criteria will need human intervention?
   1. Duplication, unambiguous name, comments, law of demeter, etc...
