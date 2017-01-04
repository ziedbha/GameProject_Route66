import static org.junit.Assert.*;
import org.junit.Test;

//JUnit tests for the cooldown class. Edge cases considered involve an invalid value for cooldown (as an example)
public class CooldownTest {

    @Test
    public void workingCooldowntest() {
        boolean thrown = false;
         try {  
            Cooldown testCool = new Cooldown(18); //making a cooldown (18 ticks)
            Boolean state = false; //state that is being put undercooldown
            testCool.startCooldown();
            int i = 0;
            while (i < 18) {
                i += 1;
                testCool.continueCooldown();
            } //cooldown should've ended
            if (testCool.cooldownEnded()) {
                state = true;
            }
            assertEquals(state, true); 
         } catch (IllegalArgumentException e) {
             thrown = true;
         }
         assertFalse(thrown);
    }
    
    @Test
    public void stillRunningCooldowntest() {
        boolean thrown = false;
            try{
                Cooldown testCool = new Cooldown(18); //making a cooldown (18 ticks)
                Boolean state = false; //state that is being put undercooldown
                testCool.startCooldown();
                int i = 0;
                while (i < 17) {
                    i += 1;
                    testCool.continueCooldown();
                } //cooldown should still have one more round
                if (testCool.cooldownEnded()) {
                    state = true;
                }
                assertFalse(state); 
            } catch (IllegalArgumentException e) {
                thrown = true;
            }
            assertFalse(thrown);
    }
    
    @Test
    public void noEffectCooldowntest() {
        boolean thrown = false;
            try{
                Cooldown testCool = new Cooldown(18); //making a cooldown (18 ticks)
                Boolean state = true; //state that is being put undercooldown. It is initially true
                //so cooldown should not affect this
                testCool.startCooldown();
                int i = 0;
                while (i < 15) {
                    i += 1;
                    testCool.continueCooldown();
                } //cooldown should still be running
                if (testCool.cooldownEnded()) {
                    state = true;
                }
                assertTrue(state); 
            } catch (IllegalArgumentException e) {
                thrown = true;
            }
            assertFalse(thrown);
    }
    
    @Test
    public void noCooldowntest() {
        boolean thrown = false;
        try {
            Cooldown testCool = new Cooldown(0); //making a cooldown (0 ticks, should be incorrect)
            Boolean state = false; //state that is being put undercooldown
            testCool.startCooldown();
            int i = 0;
            while (i < 18) {
                i += 1;
                testCool.continueCooldown();
            } //cooldown should still have one more round
            if (testCool.cooldownEnded()) {
                state = true;
            }
            assertEquals(state, true);
          } catch (IllegalArgumentException e) {
            thrown = true;
          }
          assertTrue(thrown);
    }
    
    @Test
    public void negativeCooldowntest() {
        boolean thrown = false;
        try {
            Cooldown testCool = new Cooldown(-90); //making a cooldown (-90 ticks, should be incorrect)
            Boolean state = false; //state that is being put undercooldown
            testCool.startCooldown();
            int i = 0;
            while (i < 90) {
                i += 1;
                testCool.continueCooldown();
            } //cooldown should still have one more round
            if (testCool.cooldownEnded()) {
                state = true;
            }
            assertEquals(state, true);
          } catch (IllegalArgumentException e) {
            thrown = true;
          }
          assertTrue(thrown);
    }
}
